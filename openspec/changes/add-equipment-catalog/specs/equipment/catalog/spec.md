## Purpose

定义受控的摄影器材事实目录，为用户器材选择和后续AI可执行性校验提供统一的机身、镜头、附件与兼容性数据。

## ADDED Requirements

### Requirement: P0 camera catalog
The catalog SHALL contain Sony α6700, α7 IV, α7 V, α7C II and Nikon Z5 II, Z6 II, Z7 II, Z8 with brand, model, mount and sensor format.

#### Scenario: List P0 cameras
- **WHEN** an authenticated client requests the P0 camera catalog
- **THEN** all eight models are returned with APS-C or full-frame metadata

### Requirement: Lens and accessory catalog
The catalog SHALL represent Sony, Nikon, Canon, Tamron, Sigma and Viltrox lenses plus tripod, on-camera flash, off-camera flash, ND, CPL, reflector and continuous light accessory types.

#### Scenario: Query by brand
- **WHEN** the client filters lenses by Sigma
- **THEN** only matching catalog items and their mount, focal length, aperture and format fields are returned

### Requirement: Compatibility facts
The catalog SHALL expose native mount and format compatibility and SHALL NOT mark a Canon native lens as directly compatible with a P0 Sony or Nikon body.

#### Scenario: Cross-mount query
- **WHEN** compatibility is evaluated between a Canon native lens and a Nikon Z body without an adapter record
- **THEN** the combination is reported as incompatible
