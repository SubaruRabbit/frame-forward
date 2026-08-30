package com.frameforward.course;

public final class CourseRules {
  public static final String UNVERIFIED_MENU_DISCLAIMER = "菜单路径未获当前固件证实，请核对相机当前固件和官方说明书。";
  private CourseRules() {}
  public static CourseVersion regenerate(CourseVersion original, String nextVersionId) { return new CourseVersion(nextVersionId, java.util.List.of()); }
  public static String safeEquipmentText(String model, String material) {
    return model + "：" + material + "。" + UNVERIFIED_MENU_DISCLAIMER;
  }
}
