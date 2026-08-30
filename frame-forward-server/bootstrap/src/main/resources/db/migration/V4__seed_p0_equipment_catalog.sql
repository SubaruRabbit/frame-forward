INSERT INTO catalog_cameras (id, brand, model, mount, sensor_format) VALUES
('sony-a6700', 'Sony', 'α6700', 'E', 'APS_C'), ('sony-a7-iv', 'Sony', 'α7 IV', 'E', 'FULL_FRAME'),
('sony-a7-v', 'Sony', 'α7 V', 'E', 'FULL_FRAME'), ('sony-a7c-ii', 'Sony', 'α7C II', 'E', 'FULL_FRAME'),
('nikon-z5-ii', 'Nikon', 'Z5 II', 'Z', 'FULL_FRAME'), ('nikon-z6-ii', 'Nikon', 'Z6 II', 'Z', 'FULL_FRAME'),
('nikon-z7-ii', 'Nikon', 'Z7 II', 'Z', 'FULL_FRAME'), ('nikon-z8', 'Nikon', 'Z8', 'Z', 'FULL_FRAME');

INSERT INTO catalog_lenses (id, brand, model, mount, focal_length_min_mm, focal_length_max_mm, maximum_aperture, sensor_format) VALUES
('sony-fe-24-70-gm-ii', 'Sony', 'FE 24-70mm F2.8 GM II', 'E', 24, 70, 2.8, 'FULL_FRAME'),
('nikon-z-24-70-s', 'Nikon', 'NIKKOR Z 24-70mm f/2.8 S', 'Z', 24, 70, 2.8, 'FULL_FRAME'),
('canon-rf-24-70-l', 'Canon', 'RF 24-70mm F2.8 L IS USM', 'RF', 24, 70, 2.8, 'FULL_FRAME'),
('tamron-17-70-di-iii-a', 'Tamron', '17-70mm F/2.8 Di III-A VC RXD', 'E', 17, 70, 2.8, 'APS_C'),
('sigma-18-50-dc-dn', 'Sigma', '18-50mm F2.8 DC DN', 'E', 18, 50, 2.8, 'APS_C'),
('viltrox-27-f12-pro', 'Viltrox', 'AF 27mm F1.2 Pro', 'E', 27, 27, 1.2, 'APS_C');

INSERT INTO catalog_accessory_types (id, display_name) VALUES
('tripod', 'Tripod'), ('on-camera-flash', 'On-camera flash'), ('off-camera-flash', 'Off-camera flash'),
('nd-filter', 'ND filter'), ('cpl-filter', 'CPL filter'), ('reflector', 'Reflector'), ('continuous-light', 'Continuous light');
