-- 1. Enable PostGIS Extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. Add location column to restaurants
ALTER TABLE restaurants ADD COLUMN location GEOMETRY(Point, 4326);

-- 3. Migrate existing data (Lat/Lon -> Point)
-- Note: PostGIS uses (Longitude, Latitude) order for points!
UPDATE restaurants 
SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326) 
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- 4. Add location column to couriers
ALTER TABLE couriers ADD COLUMN location GEOMETRY(Point, 4326);

-- 5. Migrate existing data
UPDATE couriers 
SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326) 
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- 6. Create Spatial Indexes for performance
CREATE INDEX idx_restaurants_location ON restaurants USING GIST (location);
CREATE INDEX idx_couriers_location ON couriers USING GIST (location);

-- 7. Drop legacy columns
ALTER TABLE restaurants DROP COLUMN latitude, DROP COLUMN longitude;
ALTER TABLE couriers DROP COLUMN latitude, DROP COLUMN longitude;
