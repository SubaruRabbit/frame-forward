package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest @AutoConfigureMockMvc
class AiTaskIntegrationTests {
  @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired JdbcTemplate jdbc; @Autowired AiTaskRuntime runtime;

  @Test void tasksAreOwnedIdempotentTracedValidatedAndRecoverable() throws Exception {
    var name="ai"+UUID.randomUUID().toString().replace("-", "").substring(0,10);
    var registration= mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("username",name,"email",name+"@example.com","password","ValidPass1!")))).andExpect(status().isCreated()).andReturn();
    var token=json.readTree(registration.getResponse().getContentAsString()).get("accessToken").asText();
    var body=Map.of("operationType","coach","input",Map.of("mockOutput",Map.of()));
    var first= mvc.perform(post("/ai/tasks").header("Authorization","Bearer "+token).header("Idempotency-Key","same-key").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isAccepted()).andReturn();
    var taskId=json.readTree(first.getResponse().getContentAsString()).get("taskId").asText();
    var duplicate= mvc.perform(post("/ai/tasks").header("Authorization","Bearer "+token).header("Idempotency-Key","same-key").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isAccepted()).andReturn();
    org.junit.jupiter.api.Assertions.assertEquals(taskId,json.readTree(duplicate.getResponse().getContentAsString()).get("taskId").asText());
    JsonNode task=null; for(int i=0;i<40;i++){ var result=mvc.perform(get("/ai/tasks/{id}",taskId).header("Authorization","Bearer "+token)).andExpect(status().isOk()).andReturn(); task=json.readTree(result.getResponse().getContentAsString()); if("FAILED".equals(task.get("state").asText())) break; Thread.sleep(25); }
    org.junit.jupiter.api.Assertions.assertEquals("FAILED",task.get("state").asText());
    org.junit.jupiter.api.Assertions.assertFalse(task.has("result") && !task.get("result").isNull());
    org.junit.jupiter.api.Assertions.assertEquals("INVALID_MODEL_OUTPUT",task.get("errorCode").asText());
    org.junit.jupiter.api.Assertions.assertEquals("qwen-plus",task.get("trace").get("modelId").asText());
    mvc.perform(get("/ai/tasks/{id}/events",taskId).header("Authorization","Bearer "+token).accept(MediaType.TEXT_EVENT_STREAM)).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("progress")));
    jdbc.update("UPDATE ai_tasks SET state = 'RUNNING' WHERE id = ?",taskId); runtime.recoverInterruptedTasks();
    mvc.perform(get("/ai/tasks/{id}",taskId).header("Authorization","Bearer "+token)).andExpect(jsonPath("$.state").value("QUEUED"));
    mvc.perform(get("/ai/tasks/{id}",taskId)).andExpect(status().isUnauthorized());
  }
}
