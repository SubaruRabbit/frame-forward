import React from 'react';
import {act, create} from 'react-test-renderer';
import {ShootingPlanCards, type ShootingPlan} from './ShootingPlanCards';

const plans: ShootingPlan[] = [
  {label:'SAFE',recommended:true,position:'人行道内侧',distance:'2 米',cameraHeight:'胸口高度',orientation:'竖构图',focalLengthMm:35,exposure:{aperture:'f/2.8',shutterSpeed:'1/250s',iso:400,startingPoint:true},metering:'评价测光',focus:'单次自动对焦',driveMode:'单张',whiteBalance:'自动',composition:'引导线',pose:'自然站立',accessoryUse:'无需附件',steps:['确认安全','完成试拍']},
  {label:'ATMOSPHERIC',recommended:false,position:'广场',distance:'3 米',cameraHeight:'眼睛高度',orientation:'横构图',focalLengthMm:50,exposure:{aperture:'f/2.8',shutterSpeed:'1/125s',iso:800,startingPoint:true},metering:'评价测光',focus:'单次自动对焦',driveMode:'单张',whiteBalance:'自动',composition:'留白',pose:'侧身',accessoryUse:'无需附件',steps:['确认安全']},
];
test('默认展开推荐方案并显示所有可执行字段和起始值标签', () => { let tree: ReturnType<typeof create>; act(() => { tree=create(<ShootingPlanCards plans={plans}/>); }); expect(tree!.root.findByProps({testID:'shooting-plan-0'})).toBeTruthy(); expect(JSON.stringify(tree!.toJSON())).toContain('曝光起始值'); expect(JSON.stringify(tree!.toJSON())).toContain('附件'); });
