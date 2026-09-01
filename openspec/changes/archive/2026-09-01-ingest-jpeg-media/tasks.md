## 1. Upload contract and storage

- [x] 1.1 `contracts`: define JPEG upload, progress and media metadata responses and verify the 50 MB constraint is documented and validated.
- [x] 1.2 `frame-forward-server/media`: implement temporary upload, MIME/decode/size checks and atomic local-file commit and verify corrupt and oversized fixtures are rejected.
- [x] 1.3 `frame-forward-server/media`: persist owner, content hash and dimensions and verify a different user cannot fetch the media record.
- [x] 1.4 `frame-forward-server/media`: parse allowed EXIF, correct analysis orientation and strip GPS from the AI copy and verify with tagged JPEG fixtures.

## 2. Android import

- [x] 2.1 `frame-forward-app/features/photo-import`: connect the system file picker and upload progress UI and verify interrupted upload offers a successful retry.
