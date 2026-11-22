-- Preserve existing departure/removal rows as the history expands to joins.
RENAME TABLE trip_member_departures TO trip_member_events;
