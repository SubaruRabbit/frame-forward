## 1. User equipment service

- [x] 1.1 `contracts`: define user equipment CRUD and primary-camera operations and verify OpenAPI validation passes.
- [x] 1.2 `frame-forward-server/equipment`: persist owned catalog references and verify users cannot read or delete another user's equipment.
- [x] 1.3 `frame-forward-server/equipment`: implement atomic primary-camera switching and verify the database never exposes two primary cameras for one user.
- [x] 1.4 `frame-forward-server/equipment`: expose executable body-lens combinations and verify incompatible pairs are flagged and excluded from defaults.

## 2. Equipment UI

- [x] 2.1 `frame-forward-app/features/equipment`: add catalog search and add/remove flows and verify the owned-equipment list updates after each action.
- [x] 2.2 `frame-forward-app/features/equipment`: add primary-camera switching and incompatibility feedback and verify the selected primary state survives reload.
