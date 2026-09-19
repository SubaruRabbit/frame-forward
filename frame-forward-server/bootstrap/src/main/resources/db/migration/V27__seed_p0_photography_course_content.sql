CREATE TABLE course_definitions (
  id VARCHAR(80) PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  category VARCHAR(32) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT chk_course_category CHECK (category IN ('BASICS', 'MIRRORLESS', 'EQUIPMENT', 'MODEL'))
) COMMENT='课程定义表';

CREATE TABLE course_chapters (
  id VARCHAR(80) PRIMARY KEY,
  content_version_id VARCHAR(36) NOT NULL,
  title VARCHAR(160) NOT NULL,
  sequence_number INT NOT NULL,
  CONSTRAINT fk_course_chapter_version FOREIGN KEY (content_version_id) REFERENCES course_content_versions(id),
  CONSTRAINT uq_course_chapter_sequence UNIQUE KEY (content_version_id, sequence_number),
  CONSTRAINT chk_course_chapter_sequence CHECK (sequence_number > 0)
) COMMENT='课程内容版本章节表';

CREATE TABLE course_lessons (
  id VARCHAR(80) PRIMARY KEY,
  chapter_id VARCHAR(80) NOT NULL,
  title VARCHAR(160) NOT NULL,
  sequence_number INT NOT NULL,
  objective VARCHAR(500) NOT NULL,
  content LONGTEXT NOT NULL,
  correct_example TEXT NOT NULL,
  incorrect_example TEXT NOT NULL,
  exercise_json JSON NOT NULL,
  assignment_text TEXT NOT NULL,
  CONSTRAINT fk_course_lesson_chapter FOREIGN KEY (chapter_id) REFERENCES course_chapters(id),
  CONSTRAINT uq_course_lesson_sequence UNIQUE KEY (chapter_id, sequence_number),
  CONSTRAINT chk_course_lesson_sequence CHECK (sequence_number > 0)
) COMMENT='课程章节课时内容表';

INSERT INTO course_definitions (id, title, category, created_at) VALUES
('p0-basics', '摄影基础', 'BASICS', '2026-09-10 00:00:00.000000'),
('p0-mirrorless', '微单操作', 'MIRRORLESS', '2026-09-10 00:00:00.000000'),
('p0-equipment', '器材选择', 'EQUIPMENT', '2026-09-10 00:00:00.000000'),
('p0-sony-a6700', 'Sony α6700 入门', 'MODEL', '2026-09-10 00:00:00.000000'),
('p0-sony-a7-iv', 'Sony α7 IV 入门', 'MODEL', '2026-09-10 00:00:00.000000'),
('p0-sony-a7-v', 'Sony α7 V 入门', 'MODEL', '2026-09-10 00:00:00.000000'),
('p0-sony-a7c-ii', 'Sony α7C II 入门', 'MODEL', '2026-09-10 00:00:00.000000'),
('p0-nikon-z5-ii', 'Nikon Z5 II 入门', 'MODEL', '2026-09-10 00:00:00.000000'),
('p0-nikon-z6-ii', 'Nikon Z6 II 入门', 'MODEL', '2026-09-10 00:00:00.000000'),
('p0-nikon-z7-ii', 'Nikon Z7 II 入门', 'MODEL', '2026-09-10 00:00:00.000000'),
('p0-nikon-z8', 'Nikon Z8 入门', 'MODEL', '2026-09-10 00:00:00.000000');

INSERT INTO course_content_versions (id, course_id, content_version, model_id, prompt_version, source_material_version, created_at) VALUES
('00000000-0000-0000-0000-000000000101', 'p0-basics', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'prd-v1.0.0-approved', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000102', 'p0-mirrorless', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'prd-v1.0.0-approved', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000103', 'p0-equipment', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'prd-v1.0.0-approved', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000104', 'p0-sony-a6700', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'sony-a6700-catalog-v1', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000105', 'p0-sony-a7-iv', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'sony-a7-iv-catalog-v1', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000106', 'p0-sony-a7-v', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'sony-a7-v-catalog-v1', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000107', 'p0-sony-a7c-ii', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'sony-a7c-ii-catalog-v1', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000108', 'p0-nikon-z5-ii', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'nikon-z5-ii-catalog-v1', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000109', 'p0-nikon-z6-ii', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'nikon-z6-ii-catalog-v1', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000110', 'p0-nikon-z7-ii', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'nikon-z7-ii-catalog-v1', '2026-09-10 00:00:00.000000'),
('00000000-0000-0000-0000-000000000111', 'p0-nikon-z8', 'p0-2026-09', 'qwen3.7-plus', 'p0-course-v1', 'nikon-z8-catalog-v1', '2026-09-10 00:00:00.000000');

INSERT INTO course_chapters (id, content_version_id, title, sequence_number) VALUES
('basics-core', '00000000-0000-0000-0000-000000000101', '看见画面', 1),
('mirrorless-core', '00000000-0000-0000-0000-000000000102', '把相机变成工具', 1),
('equipment-core', '00000000-0000-0000-0000-000000000103', '依据目的选择器材', 1),
('sony-a6700-core', '00000000-0000-0000-0000-000000000104', 'Sony α6700 核心操作', 1),
('sony-a7-iv-core', '00000000-0000-0000-0000-000000000105', 'Sony α7 IV 核心操作', 1),
('sony-a7-v-core', '00000000-0000-0000-0000-000000000106', 'Sony α7 V 核心操作', 1),
('sony-a7c-ii-core', '00000000-0000-0000-0000-000000000107', 'Sony α7C II 核心操作', 1),
('nikon-z5-ii-core', '00000000-0000-0000-0000-000000000108', 'Nikon Z5 II 核心操作', 1),
('nikon-z6-ii-core', '00000000-0000-0000-0000-000000000109', 'Nikon Z6 II 核心操作', 1),
('nikon-z7-ii-core', '00000000-0000-0000-0000-000000000110', 'Nikon Z7 II 核心操作', 1),
('nikon-z8-core', '00000000-0000-0000-0000-000000000111', 'Nikon Z8 核心操作', 1);

INSERT INTO course_lessons (id, chapter_id, title, sequence_number, objective, content, correct_example, incorrect_example, exercise_json, assignment_text) VALUES
('basics-good-photo', 'basics-core', '什么是一张好照片', 1, '用主体、光线、瞬间和表达判断照片是否有效。', '好照片先让观众知道你要表达什么，再用光线、构图和瞬间支撑这个重点。技术参数服务于表达，而不是评分表。', '人物眼神清晰、背景安静、光线把注意力引向面部。', '主体不明确，画面中每个物体同样抢眼。', JSON_OBJECT('question', '照片参数正确就一定是好照片。', 'answer', false, 'explanation', '技术正确只是基础，表达和主体同样重要。'), '拍摄两张同一场景照片：一张明确主体，一张故意让主体不清楚；写下差异。'),
('basics-subject', 'basics-core', '主体与视觉中心', 2, '通过距离、明暗、色彩和清晰度建立视觉中心。', '先决定主体，再用靠近、留白、对比和焦点把观众视线带到主体。拍摄前问自己：第一眼应该看到什么？', '主体占据明确位置，背景亮度和颜色不与主体竞争。', '主体被杂物遮挡，边缘出现比主体更亮的区域。', JSON_OBJECT('question', '主体一定要放在画面正中央。', 'answer', false, 'explanation', '三分法、留白和居中都可用，关键是视觉中心清楚。'), '用同一物体拍摄居中、三分法和大量留白三张照片，选择最有效的一张。'),
('basics-simplify', 'basics-core', '画面简化与构图', 3, '删除与主体无关的信息并使用前景、线条和层次组织画面。', '移动拍摄位置比事后裁切更有效。观察边缘，清理杂物；用前景制造层次，用引导线带向主体。', '画面边缘干净，前景或线条自然指向主体。', '背景中有切断人物头部的线条和无关高亮。', JSON_OBJECT('question', '拍摄前检查画面四角能减少构图错误。', 'answer', true, 'explanation', '边缘常暴露杂物、截断和干扰线条。'), '找一条道路、栏杆或光影线条，将它作为引导线拍摄一张照片。'),
('basics-light', 'basics-core', '光线与曝光三要素', 4, '用光圈、快门和 ISO 在清晰度、景深和噪点之间取舍。', '光圈影响景深，快门控制运动凝固或拖影，ISO 用于补足曝光。先为拍摄意图确定快门或景深，再调整其余参数。', '拍摄奔跑主体时提高快门，必要时再提高 ISO。', '为避免 ISO 而使用过慢快门，导致主体模糊。', JSON_OBJECT('question', '提高 ISO 可以在相同光线下使用更快快门。', 'answer', true, 'explanation', 'ISO 提高会增加噪点，但可换取更快快门。'), '分别用快门优先和光圈优先拍摄一张照片，记录参数和画面变化。'),
('basics-story', 'basics-core', '情绪、故事与常见错误', 5, '用拍摄距离、时刻和色彩让照片传达情绪，并识别常见构图错误。', '故事来自关系和时刻。远景交代环境，中景呈现关系，近景强调情绪；避免地平线倾斜、主体被切断和背景抢眼。', '等待人物与环境产生互动后再按快门。', '只因主体摆好姿势就按快门，忽略环境关系。', JSON_OBJECT('question', '同一主题只拍一张就足以得到故事感。', 'answer', false, 'explanation', '改变距离和等待瞬间能提供更完整的叙事选择。'), '为一个日常场景拍摄远、中、近三张照片，挑选能讲述关系的一组。'),
('mirrorless-controls', 'mirrorless-core', '微单结构与持机', 1, '认识镜头、卡口、取景器、快门与拨盘，并建立稳定持机姿势。', '安装镜头前关闭电源，确认卡口对齐。双手分别承担握持和托镜职责，肘部靠近身体以减少抖动。', '托住镜头并用取景器或稳定支点拍摄。', '单手举相机、手臂伸直并在暗处使用慢快门。', JSON_OBJECT('question', '换镜头时应尽量缩短机身开口暴露时间。', 'answer', true, 'explanation', '这能降低灰尘进入机身的概率。'), '在安全环境中练习装卸镜头，并用三种持机姿势拍摄同一静物。'),
('mirrorless-focus', 'mirrorless-core', '自动对焦与追踪', 2, '根据静态主体或运动主体选择对焦区域和连续对焦策略。', '静态主体优先单次对焦和小区域；运动主体使用连续对焦和追踪。确认焦点落在人物最近的眼睛或关键部位。', '拍摄行人时使用连续对焦并连续观察焦点框。', '使用单次对焦锁定后让运动主体离开焦平面。', JSON_OBJECT('question', '运动主体离镜头距离变化时，连续对焦通常比单次对焦更合适。', 'answer', true, 'explanation', '连续对焦会持续调整焦点距离。'), '拍摄一位缓慢走动的人或宠物，连续拍摄五张并检查焦点命中。'),
('mirrorless-exposure', 'mirrorless-core', '曝光模式与测光', 3, '在程序、光圈优先、快门优先和手动模式间作出与拍摄意图一致的选择。', '先使用光圈优先控制景深，使用快门优先控制运动；高反差环境要查看直方图和高光警告，必要时进行曝光补偿。', '逆光人像略加曝光补偿并检查脸部亮度。', '只看屏幕亮度而不检查高光，导致天空完全过曝。', JSON_OBJECT('question', '曝光补偿可以在半自动模式中调整相机给出的亮度。', 'answer', true, 'explanation', '它不会直接替代你的景深或快门意图。'), '在逆光场景分别拍摄 0、+1 和 -1 曝光补偿，比较主体和高光。'),
('mirrorless-files', 'mirrorless-core', '白平衡、文件与备份', 4, '理解白平衡、JPEG、RAW+JPEG 和安全备份的关系。', '白平衡影响色彩倾向；JPEG 便于分享，RAW+JPEG 兼顾后期空间和快速查看。拍摄后至少保留两份副本。', '在混合光线下记录白平衡设置并保留 RAW+JPEG。', '把唯一一张存储卡当作长期备份。', JSON_OBJECT('question', 'RAW+JPEG 可以同时满足快速查看与后期处理需求。', 'answer', true, 'explanation', '两种文件承担不同用途。'), '拍摄一组 RAW+JPEG，导入后确认文件命名和备份位置。'),
('equipment-focal', 'equipment-core', '焦距、视角与镜头选择', 1, '依据主体距离、画幅和想要的透视选择广角、标准或长焦镜头。', '焦距改变视角，不直接改变透视；透视由拍摄距离决定。APS-C 机身使用全画幅镜头时要按等效视角理解构图。', '拍摄环境人像时先选择能容纳环境的焦段，再调整距离。', '为了让脸更小只换长焦却不改变拍摄距离。', JSON_OBJECT('question', '改变拍摄距离会改变透视关系。', 'answer', true, 'explanation', '镜头焦距主要改变取景范围。'), '用两个焦段在不同距离拍摄同一人物，比较背景和面部比例。'),
('equipment-compatible', 'equipment-core', '卡口、画幅与兼容性', 2, '依据卡口和画幅确认镜头是否可直接使用，并识别裁切风险。', '镜头必须与机身卡口直接兼容才可作为可执行方案。全画幅机身使用 APS-C 镜头时可能裁切或暗角；没有明确转接方案时不假设跨卡口可用。', 'Sony E 卡口机身选择标明 E 卡口的镜头，并确认画幅覆盖。', '把 RF 卡口镜头默认装在 Sony E 卡口机身上。', JSON_OBJECT('question', '卡口不同的镜头在没有明确转接方案时不能视为可直接使用。', 'answer', true, 'explanation', '机械和电子兼容性都不能凭空假设。'), '检查自己的一支镜头和机身：写下卡口、画幅与适合的拍摄类型。'),
('equipment-accessories', 'equipment-core', '附件与拍摄准备', 3, '在三脚架、滤镜、反光板和灯光之间选择与场景匹配的附件。', '三脚架解决稳定与长曝光，ND 滤镜控制强光下的快门和光圈，CPL 降低非金属反光。附件应服务于画面目标。', '拍摄流水长曝光时使用三脚架和合适的 ND 滤镜。', '为了使用新附件而让拍摄流程更复杂。', JSON_OBJECT('question', 'CPL 的主要作用之一是减弱玻璃和水面的反光。', 'answer', true, 'explanation', '使用时需旋转观察效果变化。'), '为一个室外或室内场景列出一件真正需要的附件及原因。'),
('sony-a6700-focus', 'sony-a6700-core', 'α6700 对焦与拍摄准备', 1, '为静态和运动主体建立可靠焦点，并在开始拍摄前确认电池、存储卡和镜头。', '根据主体选择对焦区域和连续对焦，先在取景器中确认焦点框再拍摄。菜单名称和位置会随固件变化，请以相机当前固件和官方说明为准。', '拍摄前确认主体眼部焦点和剩余存储空间。', '只依赖自动模式，不检查焦点落点。', JSON_OBJECT('question', '开始连续拍摄前检查焦点落点可以减少整组失焦。', 'answer', true, 'explanation', '相机识别并不等于每次都命中预期部位。'), '拍摄静物与缓慢移动主体各五张，复盘焦点命中率。'),
('sony-a7-iv-focus', 'sony-a7-iv-core', 'α7 IV 对焦与曝光检查', 1, '结合取景器、连续对焦和曝光检查完成稳定拍摄。', '先设定拍摄意图，再选择对焦与曝光模式。菜单名称和位置会随固件变化，请以相机当前固件和官方说明为准。', '人像中确认最近眼睛清晰并检查高光。', '只在拍摄结束后才发现曝光或焦点问题。', JSON_OBJECT('question', '查看高光警告有助于避免重要区域不可恢复的过曝。', 'answer', true, 'explanation', '高反差场景尤其需要检查。'), '在明暗反差场景拍摄三张，记录对焦模式与曝光补偿。'),
('sony-a7-v-focus', 'sony-a7-v-core', 'α7 V 对焦与固件提示', 1, '建立以拍摄意图为中心的对焦和参数检查流程。', '先确认当前固件与官方说明，再设置对焦和曝光。未验证的菜单路径不应被当作操作事实，请以相机当前固件和官方说明为准。', '使用已确认的功能并在拍摄前完成焦点检查。', '根据网络截图直接修改未知菜单。', JSON_OBJECT('question', '相机菜单路径可能因固件版本不同而变化。', 'answer', true, 'explanation', '应优先以当前固件和官方资料核对。'), '拍摄前记录固件版本，并用一种已确认的对焦方式完成十张练习。'),
('sony-a7c-ii-focus', 'sony-a7c-ii-core', 'α7C II 轻量化拍摄', 1, '在轻量机身上通过稳定持机、对焦确认和参数预设提高成功率。', '轻量化不意味着可以忽略稳定。为常用场景准备参数起点，拍摄时检查焦点和快门速度。菜单名称和位置会随固件变化，请以相机当前固件和官方说明为准。', '低光下提高快门优先级并保持稳定握持。', '因机身轻便而在过慢快门下随意手持。', JSON_OBJECT('question', '较轻的机身仍需要根据焦段和主体运动确定安全快门。', 'answer', true, 'explanation', '抖动与主体运动不会因机身变轻而消失。'), '用常用镜头在室内拍摄十张，标出最慢仍清晰的快门速度。'),
('nikon-z5-ii-focus', 'nikon-z5-ii-core', 'Z5 II 对焦与曝光', 1, '用连续对焦、取景器确认和曝光检查完成基础拍摄。', '为静态和运动主体选择对应对焦策略，并通过回放检查焦点。菜单名称和位置会随固件变化，请以相机当前固件和官方说明为准。', '回放放大检查关键细节是否清晰。', '只看缩略图就认为对焦成功。', JSON_OBJECT('question', '放大回放是检查关键焦点是否清晰的可靠方法。', 'answer', true, 'explanation', '缩略图难以判断细小失焦。'), '分别拍摄静态与移动主体，放大回放并记录失焦原因。'),
('nikon-z6-ii-focus', 'nikon-z6-ii-core', 'Z6 II 运动主体练习', 1, '围绕运动主体选择连续对焦、连拍和安全快门。', '先保证快门速度足以冻结主体，再用连续对焦追踪。菜单名称和位置会随固件变化，请以相机当前固件和官方说明为准。', '拍摄行走的人时优先保证快门并连续检查焦点。', '为降低 ISO 牺牲必要快门速度。', JSON_OBJECT('question', '运动主体拍摄时，快门速度通常优先于降低 ISO。', 'answer', true, 'explanation', '模糊往往无法在后期修复。'), '拍摄一段运动场景，选择三张焦点和动作都清晰的照片。'),
('nikon-z7-ii-focus', 'nikon-z7-ii-core', 'Z7 II 细节与稳定', 1, '在高细节画面中通过稳定、精确对焦和曝光保护提高可用率。', '高细节拍摄会放大抖动和失焦。使用稳定姿势、合适快门和精确焦点；菜单名称和位置会随固件变化，请以相机当前固件和官方说明为准。', '静物拍摄使用稳定支撑并放大检查细节。', '在慢快门手持后只靠锐化补救。', JSON_OBJECT('question', '像素更多会让轻微抖动更容易被看见。', 'answer', true, 'explanation', '细节记录能力也会记录相机和主体运动。'), '拍摄一件静物，分别用手持和稳定支撑比较细节。'),
('nikon-z8-focus', 'nikon-z8-core', 'Z8 工作流与安全检查', 1, '在高节奏拍摄前建立电池、存储、焦点和备份检查流程。', '复杂拍摄应先准备电池、卡和文件策略，再设置对焦与曝光。菜单名称和位置会随固件变化，请以相机当前固件和官方说明为准。', '拍摄前完成存储卡、焦点和曝光检查清单。', '在重要拍摄开始后才发现卡满或电量不足。', JSON_OBJECT('question', '重要拍摄前的设备检查是工作流的一部分。', 'answer', true, 'explanation', '它减少无法重拍的技术风险。'), '为下一次拍摄写一张五项检查清单，并在现场逐项确认。');
