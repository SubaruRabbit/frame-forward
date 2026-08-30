package com.frameforward.course;

import java.util.List;

/** 不可变内容版本及其已开始学习的账户。 */
public record CourseVersion(String id, List<String> startedAccountIds) {
  public CourseVersion { startedAccountIds = List.copyOf(startedAccountIds); }
}
