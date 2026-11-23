-- Ownership is stored only in trips.owner_id.
-- trip_members contains invited participants, all with the same permissions.
DELETE tm
FROM trip_members AS tm
JOIN trips AS t ON t.id = tm.trip_id AND t.owner_id = tm.user_id;

ALTER TABLE trip_members DROP COLUMN role;
