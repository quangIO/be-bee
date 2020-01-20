CREATE OR REPLACE RULE event_log AS on UPDATE TO bee_status
    DO INSERT INTO status_event (bee_id, speed, latitude, longitude, elevation, fuel, damage)
    VALUES (NEW.bee_id, NEW.speed, NEW.latitude, NEW.longitude, NEW.elevation, NEW.fuel, NEW.damage);
