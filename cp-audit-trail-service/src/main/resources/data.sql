ALTER table audit_trail_performance ALTER COLUMN widget_id type integer USING (widget_id::integer);