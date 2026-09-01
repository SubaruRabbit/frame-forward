## 1. Catalog model

- [x] 1.1 `frame-forward-server/equipment`: add camera, lens and accessory catalog tables and verify the migration applies to an empty MySQL 8.4 database.
- [x] 1.2 `frame-forward-server/equipment`: add versioned P0 seed data and verify all eight camera models, six lens brands and seven accessory types are present.
- [x] 1.3 `frame-forward-server/equipment`: implement mount and format compatibility rules and verify native, APS-C crop and cross-mount cases.

## 2. Read contract

- [x] 2.1 `contracts`: define authenticated catalog list and filter operations and verify generated examples conform to the schema.
