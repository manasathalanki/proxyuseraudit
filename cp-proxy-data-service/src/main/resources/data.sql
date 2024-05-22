
TRUNCATE table services_directory CASCADE;

TRUNCATE table services_dynamic_parameters CASCADE;


INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
( -1, 'JSON', 'Content-Type: application/json', '?assetid=<LINEUP_IDS_CSV>', 'GET', 'com.bh.cp.proxy.handler.impl.FleetResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/assetstate/asset',-1,false ), 
( 1, 'JSON', 'Content-Type: application/json', '{"assetId":<ASSET_ID>,"fields":["flag_internal","system","group","asset_id"],"filterConditions":[{"filterOperand":{"field":"open_date"},"filterValues":[<OPEN_DATE>],"operand":[],"operation":"gt"}],"level":<ASSET_LEVEL>,"limit":"","offset":0,"sortConditions":[{"field":"open_date","order":"desc"}]}', 'POST', 'com.bh.cp.proxy.handler.impl.TrpResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/trpcases/v1/values',9,true),
( 2, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"limit":"","offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.OpenStatusResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',5,false ),
( 3, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.LastIcenterResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',29,true ),
( 4, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"limit":"","offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CaseStatusResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',7,false ),
( 6, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastUpdateDateUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastUpdateDate","order":"desc"}],"limit":"","offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CaseCriticalityResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',6,true ),
( 7, 'JSON', 'Content-Type: application/json', '{"project_ids":[<PROJECT_ID>],"plant_ids":[<PLANT_ID>],"train_ids":[<TRAIN_ID>], "lineup_ids": [<LINEUP_ID>], "machine_ids": [<MACHINE_ID_CSV>],"presets" : [<DATE_RANGE>]}', 'POST', 'com.bh.cp.proxy.handler.impl.KpiHoursResponseHandler', 'REST', 'https://dev-pears.bakerhughes.com/pearsapi/countersvc/v1/get_aggregate_project_counter_gt_data' ,26,true),
( 8, 'JSON', 'Content-Type: application/json', '{"project_ids":[<PROJECT_ID>],"plant_ids":[<PLANT_ID>],"train_ids":[<TRAIN_ID>], "lineup_ids": [<LINEUP_ID>], "machine_ids": [<MACHINE_ID_CSV>],"presets" : [<DATE_RANGE>]}', 'POST', 'com.bh.cp.proxy.handler.impl.KpiStartsResponseHandler', 'REST', 'https://dev-pears.bakerhughes.com/pearsapi/countersvc/v1/get_aggregate_project_counter_gt_data' ,27,true),
( 9, 'JSON', 'Content-Type: application/json', '{"project_ids":[<PROJECT_ID>],"plant_ids":[<PLANT_ID>],"train_ids":[<TRAIN_ID>], "lineup_ids": [<LINEUP_ID>], "machine_ids": [<MACHINE_ID_CSV>],"presets" : [<DATE_RANGE>]}', 'POST', 'com.bh.cp.proxy.handler.impl.KpiTripsResponseHandler', 'REST', 'https://dev-pears.bakerhughes.com/pearsapi/countersvc/v1/get_aggregate_project_counter_gt_data' ,28,true), 
( 10, 'JSON', 'Content-Type: application/json', '{"project":<PROJECT_ID>,"plantid":[<PLANT_ID>],"trainid":[<TRAIN_ID>], "lineupid": [<APPLICABLE_LINEUP_IDS_TEXT>],"presets":[<DATE_RANGE>]}', 'POST', 'com.bh.cp.proxy.handler.impl.MtbtResponseHandler', 'REST', 'https://dev-pears.bakerhughes.com/pearsapi/trpservice/get_mtbt_sr_rmnd_latest_value',31,true ),
( 11, 'JSON', 'Content-Type: application/json', '{"project":<PROJECT_ID>,"plantid":[<PLANT_ID>],"trainid":[<TRAIN_ID>], "lineupid": [<APPLICABLE_LINEUP_IDS_TEXT>],"presets":[<DATE_RANGE>]}', 'POST', 'com.bh.cp.proxy.handler.impl.SrResponseHandler', 'REST', 'https://dev-pears.bakerhughes.com/pearsapi/trpservice/get_mtbt_sr_rmnd_latest_value',30,true ),
( 12, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"SERIALNO"},"operation":null,"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"Status"},"operation":"eq","filterValues":["OPEN","CLOSED"]},{"filterOperand1":{"field":"lastUpdateDateUTC"},"operation":"gt","filterValues":[<OPEN_DATE>]}],"limit":null,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.KpiOpenCasesResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',32,false ),
( 13, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"SERIALNO"},"operation":null,"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"Status"},"operation":"eq","filterValues":["OPEN","CLOSED"]},{"filterOperand1":{"field":"lastUpdateDateUTC"},"operation":"gt","filterValues":[<OPEN_DATE>]}],"limit":null,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.KpiClosedCasesResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',33,false ),
( 14, 'JSON', 'Content-Type: application/json', '?assetIds=<ASSET_ID_TEXT>&cases=n&level=<ASSET_LEVEL_TEXT>', 'GET', 'com.bh.cp.proxy.handler.impl.HealthIndexResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/healthIndex/v1/values',24,true ),
( 15, 'JSON', 'Content-Type: application/json', '?assetIds=<ASSET_ID_TEXT>&cases=n&level=<ASSET_LEVEL_TEXT>', 'GET', 'com.bh.cp.proxy.handler.impl.HealthStatusResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/healthIndex/v1/values',23,true ),
( 16, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.TenPortalResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',1,false ),
( 17, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.TrainingPortalResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',2,false),
( 18, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.MaintenanceOptimizerResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',3,false),
( 19, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.EventsTimelineResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',4,false ),
( 20, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.AxCoWwOptimizationResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',10,true ),
( 21, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.FilterChangeAdvisoryResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',11,true ),
( 22, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CeCoOperatingPointResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',12,true ),
( 23, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CeCoOperatingProfileResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',13,true ),
( 24, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CeCoMapResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',14,true ),
( 25, 'JSON', 'Content-Type: application/json', '?assets=<4B_MACHINE_ID_TEXT>&recoupType=HP', 'GET', 'com.bh.cp.proxy.handler.impl.ThrustBearingLoad4BResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/thrustload/detail',15,true ),
( 26, 'JSON', 'Content-Type: application/json', '?assets=<4B_MACHINE_ID_TEXT>&recoupType=HP', 'GET', 'com.bh.cp.proxy.handler.impl.ThrustBearingLoad7BResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/thrustload/detail',16,true ),
( 27, 'JSON', 'Content-Type: application/json', '?assets=<4B_MACHINE_ID_TEXT>&recoupType=HP', 'GET', 'com.bh.cp.proxy.handler.impl.ThrustBearingLoad4BSummaryResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/thrustload/detail',17,true ),
( 28, 'JSON', 'Content-Type: application/json', '?assets=<4B_MACHINE_ID_TEXT>&recoupType=HP', 'GET', 'com.bh.cp.proxy.handler.impl.ThrustBearingLoad7BSummaryResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/thrustload/detail',18,true ),
( 29, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.SpinningReserveResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',19,true ),
( 30, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.SpinningReserveSummaryResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',20,true ),
( 31, 'JSON', 'Content-Type: application/json', '?param=[{"PR":"ENI_COR","PL":"CORAL_FLNG"}]', 'GET', 'com.bh.cp.proxy.handler.impl.CarbonOptimizerResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/carbonOptimizer/groups' ,21,true),
( 32, 'JSON', 'Content-Type: application/json', '?from=<FROM_DT_UTC_IN_MS>&to=<TO_DT_UTC_IN_MS>&vid=<DLE_HEALTH_MACHINE_VID_TEXT>', 'GET', 'com.bh.cp.proxy.handler.impl.DLEHealthStatusResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/dleHealthStatus/v1/healthStatus',22,true ),
( 33, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastNotificationUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastNotificationUTC","order":"desc"}],"limit":1,"offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.OmmPortalResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',25,false ),
( -4, 'JSON', 'Content-Type: application/json', '{"filterConditions":[{"filterOperand1":{"field":"machineSerialNum"},"filterValues":[<MACHINE_ID_CSV>]},{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["CLOSED"]},{"filterOperand1":{"field":"lastUpdateDateUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"limit":"","offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CaseStatusResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',-7,false ),
( -31, 'JSON', 'Content-Type: application/json', '?assets=<MACHINES>', 'GET', 'com.bh.cp.proxy.handler.impl.4CarbonOptimizerResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/carbonOptimizer/values',-21,true ),
( 34, 'JSON', 'Content-Type: application/json', '?assets=<1B_MACHINE_ID_TEXT>&recoupType=LP', 'GET', 'com.bh.cp.proxy.handler.impl.ThrustBearingLoad1BResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/thrustload/detail',34,true ),
( 35, 'JSON', 'Content-Type: application/json', '?assets=<1B_MACHINE_ID_TEXT>&recoupType=LP', 'GET', 'com.bh.cp.proxy.handler.impl.ThrustBearingLoad1BSummaryResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/thrustload/detail' ,35,true) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET widget_id=8 WHERE id=3;

UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.CarbonOptimizerResponseHandler' WHERE id=-31;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)VALUES (36,'JSON','Content-Type: application/json','{"filterConditions":[<DYNAMIC_PARAMETERS>],"sortConditions":[{"field":"issue.serialno","order":"desc"},{"field":"tcim.trp_id","order":"desc"},{"field":"icm.customer_wo","order":"desc"}],"limit": null,"offset": "0"}','POST', 'com.bh.cp.proxy.handler.impl.CaseListResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases',36,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES
(1,'projectId','{"filterOperand1": {"field": "waf.PROJECTID"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),
(2,'plantId','{"filterOperand1": {"field": "waf.plantid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),
(3,'trainId','{"filterOperand1": {"field": "waf.trainid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),
(4,'lineupId','{"filterOperand1": {"field": "issue.lineup_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),
(5,'machineId','{"filterOperand1": {"field": "issue.serialno"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),
(6,'caseNumber','{"filterOperand1": {"field": "issue.issue_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),
(7,'criticality','{"filterOperand1": {"field": "issue.criticality"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),
(8,'customerPriority','{"filterOperand1": {"field": "issue.customer_priority"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),(9,'status','{"filterOperand1": {"field": "issue.status"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),
(10,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',36),
(11,'endDate','{"filterOperand1": {"field": "issue.issue_end_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',36) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES (37,'JSON','Content-Type: application/json', '{"filterConditions":[<DYNAMIC_PARAMETERS>],"limit": null,"offset": "0"}','POST', 'com.bh.cp.proxy.handler.impl.TaskListResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/tasks',37,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) values (12,'parentCaseId','{"filterOperand1": {"field": "parentCaseId"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',37) ON CONFLICT (id) DO NOTHING;
            
INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES
 ( 38, 'JSON', 'Content-Type: application/json', '{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastUpdateDateUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastUpdateDate","order":"desc"}],"limit":"","offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CaseCriticalityResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases',0,true ), 
 ( 39, 'JSON', 'Content-Type: application/json', '{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"limit":"","offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CaseStatusResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases',0,false ), 
( 40, 'JSON', 'Content-Type: application/json', '{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"limit":"","offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.OpenStatusResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v3/cases',0,false ),
 ( 41, 'JSON', 'Content-Type: application/json', '{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field":"status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"lastUpdateDateUTC"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"lastUpdateDate","order":"desc"}],"limit":"","offset":0}', 'POST', 'com.bh.cp.proxy.handler.impl.CaseCriticalityByTitleResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases',0,true) ON CONFLICT (id) DO NOTHING;
  
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(13,'projectId','{"filterOperand1": {"field": "waf.PROJECTID"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(14,'plantId','{"filterOperand1": {"field": "waf.plantid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(15,'trainId','{"filterOperand1": {"field": "waf.trainid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(16,'lineupId','{"filterOperand1": {"field": "issue.lineup_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(17,'machineId','{"filterOperand1": {"field": "issue.serialno"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(18,'caseNumber','{"filterOperand1": {"field": "issue.issue_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(19,'criticality','{"filterOperand1": {"field": "issue.criticality"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(20,'customerPriority','{"filterOperand1": {"field": "issue.customer_priority"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(21,'status','{"filterOperand1": {"field": "issue.status"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',38),(22,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',38),(23,'endDate','{"filterOperand1": {"field": "issue.issue_end_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',38) ON CONFLICT (id) DO NOTHING;
 
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(24,'projectId','{"filterOperand1": {"field": "waf.PROJECTID"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(25,'plantId','{"filterOperand1": {"field": "waf.plantid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(26,'trainId','{"filterOperand1": {"field": "waf.trainid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(27,'lineupId','{"filterOperand1": {"field": "issue.lineup_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(28,'machineId','{"filterOperand1": {"field": "issue.serialno"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(29,'caseNumber','{"filterOperand1": {"field": "issue.issue_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(30,'criticality','{"filterOperand1": {"field": "issue.criticality"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(31,'customerPriority','{"filterOperand1": {"field": "issue.customer_priority"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(32,'status','{"filterOperand1": {"field": "issue.status"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',39),(33,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',39),(34,'endDate','{"filterOperand1": {"field": "issue.issue_end_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',39) ON CONFLICT (id) DO NOTHING;
 
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(35,'projectId','{"filterOperand1": {"field": "waf.PROJECTID"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(36,'plantId','{"filterOperand1": {"field": "waf.plantid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(37,'trainId','{"filterOperand1": {"field": "waf.trainid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(38,'lineupId','{"filterOperand1": {"field": "issue.lineup_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(39,'machineId','{"filterOperand1": {"field": "issue.serialno"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(40,'caseNumber','{"filterOperand1": {"field": "issue.issue_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(41,'criticality','{"filterOperand1": {"field": "issue.criticality"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(42,'customerPriority','{"filterOperand1": {"field": "issue.customer_priority"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(43,'status','{"filterOperand1": {"field": "issue.status"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',40),(44,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',40),(45,'endDate','{"filterOperand1": {"field": "issue.issue_end_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',40) ON CONFLICT (id) DO NOTHING;
 
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(46,'projectId','{"filterOperand1": {"field": "waf.PROJECTID"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(47,'plantId','{"filterOperand1": {"field": "waf.plantid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(48,'trainId','{"filterOperand1": {"field": "waf.trainid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(49,'lineupId','{"filterOperand1": {"field": "issue.lineup_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(50,'machineId','{"filterOperand1": {"field": "issue.serialno"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(51,'caseNumber','{"filterOperand1": {"field": "issue.issue_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(52,'criticality','{"filterOperand1": {"field": "issue.criticality"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(53,'customerPriority','{"filterOperand1": {"field": "issue.customer_priority"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(54,'status','{"filterOperand1": {"field": "issue.status"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',41),(55,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',41),(56,'endDate','{"filterOperand1": {"field": "issue.issue_end_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',41) ON CONFLICT (id) DO NOTHING; 
 
UPDATE services_directory SET input_data='?from=<FROM_DT_YYYY_MM_DD_HHMMSS_UTC>&to=<TO_DT_YYYY_MM_DD_HHMMSS_UTC>&vidList=<SPINNING_RESERVE_VID_TEXT>',method='GET',uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/apps/api/proxy/spinning-reserve' WHERE id IN(29,30);

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(57,'catagoryId','{"filterOperand1": {"field": "issue.anomaly_category"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36),(58,'caseType','{"filterOperand1": {"field": "pct.casetypeid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',36) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field": "issue.flag_internal"},"filterValues": ["N"]}],"sortConditions": [{"field" : "issue.serialno","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID =39;

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field": "issue.flag_internal"},"filterValues": ["N"]}],"sortConditions": [{"field" : "issue.serialno","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID =40;

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field": "issue.last_update_date"},""operation"":""gt"","filterValues": [<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions": [{"field" : "issue.last_update_date","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID =38;

UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.CMCaseStatusResponseHandler' WHERE ID=39;

UPDATE services_directory SET input_data='?param=<CARBON_OPTIMIZER>' WHERE id='31';

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)
 VALUES (43,'JSON','Content-Type: application/json', '{"filterConditions":[<DYNAMIC_PARAMETERS>],"limit": null,"offset": "0"}','POST',
 'com.bh.cp.proxy.handler.impl.TaskTableAPIResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/tasks',43,false) ON CONFLICT (id) DO NOTHING;


INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) values (60,'parentCaseId','{"filterOperand1": {"field": "parentCaseId"},
"filterValues": [<FILTER_VALUE>],"operation": "eq"}',43) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>],"sortConditions": [{"field" : "issue.last_update_date","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID =38;

UPDATE services_dynamic_parameters SET input_data = '{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}' WHERE field ='endDate';

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>],"operation":"eq"},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"limit":"","offset":0,"sortConditions":[{"field":"ml.date_send","order":"desc"}],"limit":1,"offset":0}',uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE id='3' ;

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"sortConditions":[{"field":"issue.issue_id","order":"desc"}],"limit":"","offset":0}',uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE id='4';

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.status"},"operation":"eq","filterValues":["CLOSED"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]},{"filterOperand1":{"field":"issue.issue_end_date"},"operation":"ge","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]},{"filterOperand1":{"field":"issue.issue_end_date"},"operation":"le","filterValues":[<TO_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"issue.issue_id","order":"desc"}],"limit":"","offset":0}',uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE id='-4';

UPDATE services_directory SET input_data='{"assetId":<ASSET_ID>,"fields":["flag_internal","system","group","status","asset_id"],"filterConditions":[{"filterOperand":{"field":"status"},"filterValues":["Deleted"],"operand":["and"],"operation":"ne"},{"filterOperand":{"field":"open_date"},"filterValues":["2022-05-01 12:13:00"],"operand":["and"],"operation":"gt"},{"filterOperand":{"field":"flag_internal"},"filterValues":["N"],"operand":[],"operation":"eq"}],"level":<ASSET_LEVEL>,"limit":"","offset":"0","sortConditions":[{"field":"open_date","order":"desc"}]}' WHERE id='1';

UPDATE services_directory SET input_data ='{"filterConditions":[<DYNAMIC_PARAMETERS>],"sortConditions":[{"field":"issue.event_date","order":"desc"}],"limit":"","offset":0}' WHERE ID=41;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES (44,'JSON','Content-Type: application/json', '<DYNAMIC_PARAMETERS>','GET', 'com.bh.cp.proxy.handler.impl.CaseTrendLinksResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/caseTrends',44,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (63,'image','image=<FILTER_VALUE>',44),(64,'issueId','issueId=<FILTER_VALUE>',44),(65,'nameLink','nameLink=<FILTER_VALUE>',44) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"sortConditions":[{"field":"issue.issue_id","order":"desc"}],"limit":"","offset":0}', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE id='2';

UPDATE services_directory SET input_data='?assets=<7B_MACHINE_ID_TEXT>&recoupType=LP' WHERE id=26;
	
UPDATE services_directory SET input_data='?assets=<7B_MACHINE_ID_TEXT>&recoupType=LP' WHERE id=28;

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1": {"field": "issue.flag_internal"},"filterValues": ["N"]}, {"filterOperand1": {"field": "issue.status"},"filterValues": ["OPEN"]}], "sortConditions": [{"field" : "issuE.event_date","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID=38;

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1": {"field": "issue.flag_internal"},"filterValues": ["N"]}, {"filterOperand1": {"field": "issue.status"},"filterValues": ["OPEN"]}], "sortConditions": [{"field" : "issuE.event_date","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID=40;

UPDATE services_dynamic_parameters SET input_data ='{"filterOperand1": {"field": "issue.customer_priority"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}' WHERE field= 'customerPriority';

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (22,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',38),(23,'endDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',38) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (44,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',40),(45,'endDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',40) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (55,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',41),(56,'endDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',41) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1": {"field": "issue.flag_internal"},"filterValues": ["N"]}, {"filterOperand1": {"field": "issue.status"},"filterValues": ["OPEN"]}], "sortConditions": [{"field" : "issuE.event_date","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID=38;

DELETE FROM services_dynamic_parameters WHERE ID IN(18,19,20,21);

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field": "issue.flag_internal"},"filterValues": ["N"]}],"sortConditions": [{"field":"issue.event_date","order":"desc"}],"limit": null,"offset": "0"}' WHERE ID=39;

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1": {"field": "issue.flag_internal"},"filterValues": ["N"]}, {"filterOperand1": {"field": "issue.status"},"filterValues": ["OPEN"]}], "sortConditions": [{"field" : "issuE.event_date","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID=40;

UPDATE services_directory SET uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE ID =40;

DELETE FROM services_dynamic_parameters WHERE ID IN(40,41,42,43,44,45);

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1": {"field": "issue.flag_internal"},"filterValues": ["N"]}, {"filterOperand1": {"field": "issue.status"},"filterValues": ["OPEN"]}], "sortConditions": [{"field" : "issue.event_date","order" : "desc"}],"limit": null,"offset": "0"}' WHERE ID=41;

DELETE FROM services_dynamic_parameters WHERE ID IN(51,52,53,54);

UPDATE services_directory SET input_data ='{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field": "issue.flag_internal"},"filterValues": ["N"]}],"sortConditions":[{"field":"issue.event_date","order":"desc"}],"limit": null,"offset": "0"}' WHERE id=36;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES (45,'JSON','Content-Type: application/json', '{ "assetId": "<ASSET_ID_TEXT>", "fields": [ "asset_id", "asset_level", "id", "type_desc", "type_id", "lineup_id", "serialno", "n_failure_mode", "failure_mode_desc", "d_event" ], "filterConditions": [ { "filterOperand": { "field": "d_event" }, "filterValues": [ <FROM_DT_DD_MM_YYYY_HHMMSS_UTC>], "operand": [], "operation": "gt" } ], "level": "<ASSET_LEVEL_TEXT>", "limit": "", "offset": "0", "sortConditions": [ { "field": "d_event", "order": "desc" } ] }','POST', 'com.bh.cp.proxy.handler.impl.EventsTimelineResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/eventService/v1/values',-4,false) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.event_date"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"issue.event_date","order":"asc"}],"limit":null,"offset":0}', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE id=19;

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]}],"sortConditions":[{"field":"issue.issue_id","order":"desc"}],"limit":"","offset":0}', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE id='12';

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.status"},"operation":"eq","filterValues":["CLOSED"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]},{"filterOperand1":{"field":"issue.issue_end_date"},"operation":"ge","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]},{"filterOperand1":{"field":"issue.issue_end_date"},"operation":"le","filterValues":[<TO_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"issue.issue_id","order":"desc"}],"limit":"","offset":0}', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE id='13';

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
(42,'JSON','Content-Type: application/json', '<DYNAMIC_PARAMETERS>','GET', 'com.bh.cp.proxy.handler.impl.CaseAttachmentAPIResponseHandler',
'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/caseAttachments',42,false) ON CONFLICT (id) DO NOTHING;
 
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (61,'attachment','attachment=<FILTER_VALUE>',42),
(62,'issueId','issueId=<FILTER_VALUE>',42) ON CONFLICT (id) DO NOTHING; 
  
INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
(45,'JSON','Content-Type: application/json', '<DYNAMIC_PARAMETERS>','GET', 'com.bh.cp.proxy.handler.impl.CaseNotificationAPIResponseHandler',
'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/caseNotifications',45,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (66,'issueId','issueId=<FILTER_VALUE>',45) ON CONFLICT (id) DO NOTHING; 

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.flag_internal"},"operation":"eq","filterValues":["N"]},{"filterOperand1":{"field":"issue.event_date"},"operation":"ge","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]},{"filterOperand1":{"field":"issue.event_date"},"operation":"le","filterValues":[<TO_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"issue.event_date","order":"desc"}],"limit":"","offset":0}',uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases' WHERE id='6';
 
UPDATE services_directory SET input_data='?vids=<APPLICABLE_MACHINE_VIDS_CSV>&recoupType=HP' WHERE ID=25;
UPDATE services_directory SET input_data='?vids=<APPLICABLE_MACHINE_VIDS_CSV>&recoupType=LP' WHERE ID=26;
UPDATE services_directory SET input_data='?vids=<APPLICABLE_MACHINE_VIDS_CSV>&recoupType=HP' WHERE ID=27;
UPDATE services_directory SET input_data='?vids=<APPLICABLE_MACHINE_VIDS_CSV>&recoupType=LP' WHERE ID=28;
UPDATE services_directory SET input_data='?from=<FROM_DT_YYYY_MM_DD_HHMMSS_UTC>&to=<TO_DT_YYYY_MM_DD_HHMMSS_UTC>&vidList=<APPLICABLE_MACHINE_VIDS_CSV>' WHERE ID=29;
UPDATE services_directory SET input_data='?from=<FROM_DT_YYYY_MM_DD_HHMMSS_UTC>&to=<TO_DT_YYYY_MM_DD_HHMMSS_UTC>&vidList=<APPLICABLE_MACHINE_VIDS_CSV>' WHERE ID=30;
UPDATE services_directory SET input_data='?from=<FROM_DT_UTC_IN_MS>&to=<TO_DT_UTC_IN_MS>&vid=<APPLICABLE_MACHINE_VIDS_CSV>' WHERE ID=32;
UPDATE services_directory SET input_data='?vids=<APPLICABLE_MACHINE_VIDS_CSV>&recoupType=LP' WHERE ID=34;
UPDATE services_directory SET input_data='?vids=<APPLICABLE_MACHINE_VIDS_CSV>&recoupType=LP' WHERE ID=35;

UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.KPIHoursResponseHandler' WHERE ID=7;
UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.KPIStartsResponseHandler' WHERE ID=8;
UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.KPITripsResponseHandler' WHERE ID=9;
UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.KPIMtbtResponseHandler' WHERE ID=10;
UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.KPISrResponseHandler' WHERE ID=11;
UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.KPIOpenCasesResponseHandler' WHERE ID=12;
UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.KPIClosedCasesResponseHandler' WHERE ID=13;

UPDATE services_directory SET input_data='{"assetId":<PROJECT_ID>,"fields":["flag_internal","system","group","status","asset_id"],"filterConditions":[{"filterOperand":{"field":"status"},"filterValues":["Deleted"],"operand":["and"],"operation":"ne"},{"filterOperand":{"field":"open_date"},"filterValues":["2022-05-01 12:13:00"],"operand":["and"],"operation":"gt"},{"filterOperand":{"field":"flag_internal"},"filterValues":["N"],"operand":[],"operation":"eq"}],"level":"projects","limit":"","offset":"0","sortConditions":[{"field":"open_date","order":"desc"}]}' WHERE id='1';

INSERT INTO services_directory ( id, communication_format, headers, input_data, method, output_handler, service_type, uri, widget_id ) VALUES ( 46, 'JSON', 'Content-Type: application/json', '{ "filterConditions": [ { "operation": "eq", "filterOperand1": { "field": "parentCaseId" }, "filterValues": [ <PARENT_CASE_IDS_TEXT> ] }, { "operation": "eq", "filterOperand1": { "field": "status" }, "filterValues": [ "OPEN" ] } ], "sortConditions": [ { "field" : "maint_task_id", "order" : "asc" } ], "limit": "", "offset": 0 }', 'POST', 'com.bh.cp.proxy.handler.impl.MaintenanceOptimizerResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/tasks',-3 ) ON CONFLICT (id) DO NOTHING;
 
INSERT INTO services_directory ( id, communication_format, headers, input_data, method, output_handler, service_type, uri, widget_id ) VALUES ( 47, 'JSON', 'Content-Type: application/json', '?gibSn=<GIB_SNS_CSV>&startDate=<TO_DT_YYYY_MM_DD_NQ>&status=2', 'GET', 'com.bh.cp.proxy.handler.impl.MaintenanceOptimizerResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/event/eventMaintenance',-31 ) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.status"},"filterValues":["OPEN"]}],"sortConditions":[{"field":"issue.issue_id","order":"desc"}],"limit":null,"offset":0}',uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases'  WHERE id=18;

ALTER TABLE services_directory ALTER COLUMN uri DROP NOT NULL;

UPDATE services_directory set uri=NULL, input_data=NULL where widget_id in (1,2,25);

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand1":{"field":"issue.serialno"},"filterValues":[<MACHINE_IDS_TEXT>]},{"filterOperand1":{"field":"issue.status"},"operation":"eq","filterValues":["OPEN"]},{"filterOperand1":{"field":"issue.event_date"},"operation":"gt","filterValues":[<FROM_DT_DD_MM_YYYY_HHMMSS_UTC>]}],"sortConditions":[{"field":"issue.event_date","order":"asc"}],"limit":null,"offset":0}' WHERE id=19;

UPDATE services_directory SET input_data='{"assetId":<PROJECT_ID>,"fields":["flag_internal","system","group","status","asset_id"],"filterConditions":[{"filterOperand":{"field":"status"},"filterValues":["Deleted"],"operand":["and"],"operation":"ne"},{"filterOperand":{"field":"open_date"},"filterValues":["2010-01-01 00:00:00"],"operand":[],"operation":"gt"}],"level":"projects","limit":"","offset":"0","sortConditions":[{"field":"open_date","order":"desc"}]}' WHERE id='1';

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
(49,'JSON','Content-Type: application/json', '<DYNAMIC_PARAMETERS>','GET', 'com.bh.cp.proxy.handler.impl.CaseCommentsAPIResponseHandler',
'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/caseComments',49,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (67,'issueId','issueId=<FILTER_VALUE>',49) ON CONFLICT (id) DO NOTHING; 

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
(48,'JSON','Content-Type: application/json','','GET','com.bh.cp.proxy.handler.impl.TypeDescriptionResponseHandler',
'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/typeDescription',48,false) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='{"assetId":<PROJECT_ID>,"fields":["flag_internal","system","group","status","asset_id"],"filterConditions":[{"filterOperand":{"field":"status"},"filterValues":["Deleted"],"operand":["and"],"operation":"ne"},{"filterOperand":{"field":"open_date"},"filterValues":["2010-01-01 00:00:00"],"operand":[],"operation":"gt"}],"level":"projects","limit":"","offset":"0","sortConditions":[{"field":"open_date","order":"desc"}]}' WHERE id='1';

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
(52,'JSON','Content-Type: application/json', '{<DYNAMIC_PARAMETERS>}','POST', 'com.bh.cp.proxy.handler.impl.EditCaseCommentResponseHandler',
'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/editComment',52,false) ON CONFLICT (id) DO NOTHING;
 
 
 INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (80,'action','"action":<FILTER_VALUE>',52) ON CONFLICT (id) DO NOTHING;
 INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (81,'caseId','"caseId":<FILTER_VALUE>',52) ON CONFLICT (id) DO NOTHING; 
 INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (82,'commentDesc','"commentDesc":<FILTER_VALUE>',52) ON CONFLICT (id) DO NOTHING; 
 INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (83,'commentId','"commentId":<FILTER_VALUE>',52) ON CONFLICT (id) DO NOTHING; 
 INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (84,'commentType','"commentType":<FILTER_VALUE>',52) ON CONFLICT (id) DO NOTHING; 
 INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (85,'commentVisible','"commentVisible":<FILTER_VALUE>',52) ON CONFLICT (id) DO NOTHING; 
 INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (86,'user','"user":<FILTER_VALUE>',52) ON CONFLICT (id) DO NOTHING; 
 INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (87,'userType','"userType":<FILTER_VALUE>',52) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)
 VALUES (55,'JSON','Content-Type: application/json', '{<DYNAMIC_PARAMETERS>}','POST', 'com.bh.cp.proxy.handler.impl.EditTaskResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/tasks/editTask',55,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (75,'caseId','"caseId":<FILTER_VALUE>',55),(76,'rootCause','"rootCause":<FILTER_VALUE>',55),(77,'status','"status":<FILTER_VALUE>',55),(78,'taskId','"taskId":<FILTER_VALUE>',55),(79,'user','"user":<FILTER_VALUE>',55)  ON CONFLICT (id) DO NOTHING;


INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)
 VALUES (50,'JSON','Content-Type: application/json', '{<DYNAMIC_PARAMETERS>,"lockType":"lock"}','POST', 'com.bh.cp.proxy.handler.impl.CaseLockResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/caseLock',50,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (68,'caseId','"caseId":<FILTER_VALUE>',50),(69,'user','"user":<FILTER_VALUE>',50)  ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (88,'attachmentId','attachId=<FILTER_VALUE>',42) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)
VALUES (56,'JSON','Content-Type: application/json', '<DYNAMIC_PARAMETERS>','POST', 'com.bh.cp.proxy.handler.impl.CaseDetailsResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/caseDetail',56,false) ON CONFLICT (id) DO NOTHING;
 
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id)
VALUES (98,'issueId','?issueId=<FILTER_VALUE>&objects=attachment,trend,notification',56) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)
 VALUES (58,'JSON','Content-Type: application/json', '{<DYNAMIC_PARAMETERS>}','POST', 'com.bh.cp.proxy.handler.impl.EditAttachmentResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/editAttachment',58,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) 
VALUES (89,'action','"action":<FILTER_VALUE>',58),(90,'attachmentTypeId','"attachmentTypeId":<FILTER_VALUE>',58),(91,'attachmentId','"attachtId":<FILTER_VALUE>',58),(92,'file','"file":<FILTER_VALUE>',58),(93,'fileName','"fileName":<FILTER_VALUE>',58),(94,'mimeType','"mimeType":<FILTER_VALUE>',58),(95,'user','"user":<FILTER_VALUE>',58),(96,'userType','"userType":<FILTER_VALUE>',58),(97,'caseId','"caseId":<FILTER_VALUE>',58) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (70,'lockType','"lockType":<FILTER_VALUE>',50)  ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='{<DYNAMIC_PARAMETERS>}' WHERE id=50;

UPDATE services_directory SET input_data='?assetId=<APPLICABLE_MACHINE_IDS_CSV>&from=<FROM_DT_YYYY_MM_DD_NQ>&to=<TO_DT_YYYY_MM_DD_NQ>', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/apps/api/proxy/ceco',method='GET' WHERE id='22';

UPDATE services_directory SET input_data='?assetId=<APPLICABLE_MACHINE_IDS_CSV>&from=<FROM_DT_YYYY_MM_DD_NQ>&to=<TO_DT_YYYY_MM_DD_NQ>', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/apps/api/proxy/ceco',method='GET' WHERE id='23';

UPDATE services_directory SET input_data='?assetId=<APPLICABLE_MACHINE_IDS_CSV>&from=<FROM_DT_YYYY_MM_DD_NQ>&to=<TO_DT_YYYY_MM_DD_NQ>', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/apps/api/proxy/ceco',method='GET' WHERE id='24';

UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.OpenCaseStatusResponseHandler' WHERE id=40;

UPDATE services_directory SET input_data='{"project":<PROJECT_ID>,"plantid":[<PLANT_ID>],"trainid":[<TRAIN_ID>], "lineupid": [<APPLICABLE_LINEUP_IDS_TEXT>],"presets":[<DATE_RANGE>]}' WHERE id IN (10,11);

UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>],"sortConditions":[{"field":"issue.event_date","order":"desc"}],"limit": null,"offset": "0"}' WHERE id=36;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (71,'flagInternal','{"filterOperand1":{"field": "issue.flag_internal"},"filterValues": [<FILTER_VALUE>]}',36) ON CONFLICT (id) DO NOTHING;

update services_dynamic_parameters SET input_data='{"filterOperand1": {"field": "issue.end_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}' where id=44;

update services_dynamic_parameters SET input_data='{"filterOperand1": {"field": "issue.end_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}' where id=45;

UPDATE services_dynamic_parameters SET input_data='{"filterOperand1": {"field": "issue.issue_end_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}' where service_id=39 and field='endDate';

UPDATE services_dynamic_parameters SET input_data='{"filterOperand1": {"field": "issue.issue_end_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}' where service_id=39 and field='startDate';

UPDATE services_directory SET input_data='?assetid=<ASSET_ID>', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/AxCoWW/v1',method='GET' WHERE id='20';

UPDATE services_directory SET input_data='?assetIdList=<APPLICABLE_MACHINE_IDS_CSV>',  method='GET',uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/apps/api/proxy/filterChangeAdvisory' WHERE widget_id='11';

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)
VALUES (59,'JSON','Content-Type: application/json', '{<DYNAMIC_PARAMETERS>}','POST', '', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/editCases',59,false) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET widget_id=0 WHERE id='36';

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
( 60, 'JSON', 'Content-Type: application/json', '?vid=<ASSET_VID>&period=M&kpiId=A', 'GET', 'com.bh.cp.proxy.handler.impl.KPIAvailabilityResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/icenter2pilot/kpi',29,true )ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
( 61, 'JSON', 'Content-Type: application/json', '?vid=<ASSET_VID>&period=M&kpiId=R', 'GET', 'com.bh.cp.proxy.handler.impl.KPIReliabilityResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/icenter2pilot/kpi',36,true )ON CONFLICT (id) DO NOTHING;


UPDATE services_directory SET input_data='{"filterConditions":[<DYNAMIC_PARAMETERS>],"sortConditions":[{"field":"issue.issue_id","order":"desc"}],"limit": null,"offset": "0"}' WHERE id=36;

UPDATE services_directory SET input_data='?vid=<ASSET_VID>&period=12M&kpiId=A' WHERE widget_id =29;

UPDATE services_directory SET input_data='?vid=<ASSET_VID>&period=12M&kpiId=R' WHERE widget_id =36;

UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.CarbonOptimizerHigherLevelResponseHandler' WHERE id=31;

UPDATE services_directory SET output_handler='com.bh.cp.proxy.handler.impl.CarbonOptimizerHigherLevelResponseHandler' WHERE id=-31;

INSERT INTO services_directory(id, communication_format, headers, input_data, is_paid_service, method, output_handler, service_type, uri, widget_id) VALUES (62, 'JSON', 'Content-Type: application/json', '?param=<CARBON_OPTIMIZER>', TRUE, 'GET', 'com.bh.cp.proxy.handler.impl.CarbonOptimizerMachineResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/carbonOptimizer/groups',38 ) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory(id, communication_format, headers, input_data, is_paid_service, method, output_handler, service_type, uri, widget_id) VALUES (-62, 'JSON', 'Content-Type: application/json', '?assets=<MACHINES>', TRUE, 'GET', 'com.bh.cp.proxy.handler.impl.CarbonOptimizerMachineResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/carbonOptimizer/values',-38) ON CONFLICT (id) DO NOTHING;
UPDATE services_directory SET uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/eventService/v1/values' WHERE widget_id =27;
UPDATE services_directory SET uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/eventService/v1/values' WHERE widget_id =28;
UPDATE services_directory SET input_data='{"assetId":"<ASSET_ID>","level":"<ASSET_LEVEL>","fields":[],"filterConditions":[{"filterOperand":{"field":"d_event"},"filterValues":["<FROM_DT_YYYY_MM_DD_HHMMSS_UTC>"],"operand":["and"],"operation":"gt"},{"filterOperand":{"field":"d_event"},"filterValues":["<TO_DT_YYYY_MM_DD_HHMMSS_UTC>"],"operand":["and"],"operation":"lt"},{"filterOperand":{"field":"type_id"},"filterValues":["START"],"operand":[],"operation":"eq"}],"limit":"","offset":0,"sortConditions":[{"field":"d_event","order":"desc"}]}' WHERE widget_id =27;
UPDATE services_directory SET input_data='{"assetId":"<ASSET_ID>","level":"<ASSET_LEVEL>","fields":[],"filterConditions":[{"filterOperand":{"field":"d_event"},"filterValues":["<FROM_DT_YYYY_MM_DD_HHMMSS_UTC>"],"operand":["and"],"operation":"gt"},{"filterOperand":{"field":"d_event"},"filterValues":["<TO_DT_YYYY_MM_DD_HHMMSS_UTC>"],"operand":["and"],"operation":"lt"},{"filterOperand":{"field":"type_id"},"filterValues":["TRIP"],"operand":[],"operation":"eq"}],"limit":"","offset":0,"sortConditions":[{"field":"d_event","order":"desc"}]}' WHERE widget_id =28;

UPDATE services_directory SET input_data='?assetid=<LINEUP_IDS_CSV>' WHERE widget_id=-1;
UPDATE services_directory SET input_data='?gibSn=<GIB_SNS_CSV>&startDate=<TO_DT_YYYY_MM_DD_NQ>&status=2' WHERE widget_id=-31;
UPDATE services_directory sd SET input_data = REPLACE(sd.input_data,'<MACHINE_ID_CSV>','<MACHINE_IDS_TEXT>') WHERE widget_id IN (-7,1,2,3,4,5,6,7,8,10,11,12,13,14,19,20,25,26,27,28,29,32,33);
UPDATE services_directory SET input_data ='?assetIds=<ASSET_ID>&cases=n&level=<ASSET_LEVEL>' WHERE widget_id IN (23,24);
UPDATE services_directory SET input_data ='{ "assetId": "<ASSET_ID>", "fields": [ "asset_id", "asset_level", "id", "type_desc", "type_id", "lineup_id", "serialno", "n_failure_mode", "failure_mode_desc", "d_event" ], "filterConditions": [ { "filterOperand": { "field": "d_event" }, "filterValues": [ <FROM_DT_DD_MM_YYYY_HHMMSS_UTC>], "operand": [], "operation": "gt" } ], "level": "<ASSET_LEVEL>", "limit": "", "offset": "0", "sortConditions": [ { "field": "d_event", "order": "desc" } ] }' WHERE widget_id=-4;
UPDATE services_directory SET input_data ='{ "filterConditions": [ { "operation": "eq", "filterOperand1": { "field": "parentCaseId" }, "filterValues": [ <PARENT_CASE_IDS_TEXT> ] }, { "operation": "eq", "filterOperand1": { "field": "status" }, "filterValues": [ "OPEN" ] } ], "sortConditions": [ { "field" : "maint_task_id", "order" : "asc" } ], "limit": "", "offset": 0 }' WHERE widget_id=-3;
UPDATE services_directory sd SET input_data = REPLACE(sd.input_data,'<PROJECT_ID>','<PROJECT_ID_TEXT>') WHERE widget_id IN (9,26,27,28,30,31);
UPDATE services_directory sd SET input_data = REPLACE(sd.input_data,'<PLANT_ID>','<PLANT_ID_TEXT>') WHERE widget_id IN (26,27,28,30,31);
UPDATE services_directory sd SET input_data = REPLACE(sd.input_data,'<TRAIN_ID>','<TRAIN_ID_TEXT>') WHERE widget_id IN (26,27,28,30,31);
UPDATE services_directory sd SET input_data = REPLACE(sd.input_data,'<LINEUP_ID>','<LINEUP_ID_TEXT>') WHERE widget_id IN (26,27,28,30,31);

UPDATE services_directory SET input_data='?lineup_ids=<LINEUP_IDS_CSV>&modules=<ASSET_LEVEL_SINGULAR>&sections=<ASSET_LEVEL_SINGULAR>&calculations=FFH&outputs=FH&required_from=<FROM_DT_UTC_IN_FH>&required_to=<TO_DT_UTC_IN_FH>&outputs=FH' WHERE widget_id =26;
UPDATE services_directory SET method='GET' WHERE widget_id =26;
UPDATE services_directory SET uri='https://dev-rmd-eclipse-api.azure.bakerhughes.com/v1/se/api/counters/get' WHERE widget_id =26;

UPDATE services_directory SET input_data='?assets=<APPLICABLE_MACHINE_IDS_CSV>' WHERE widget_id=-38;

update services_directory set input_data ='?gibSn=<GIB_SNS_CSV>&startDate=<TO_DT_YYYY_MM_DD_NQ>&status=2' where id= 47;

UPDATE services_directory SET input_data='?assets=<APPLICABLE_MACHINE_IDS_CSV>' WHERE widget_id=-38;

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES ( 63, 'JSON', 'Content-Type: application/json', '<DYNAMIC_PARAMETERS>', 'GET', 'com.bh.cp.proxy.handler.impl.DeviceDetailsResponseHandler', 'REST','https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cyberservice/v1/cyber/devicedetailslist',0,true ) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(99,'plantId','<FILTER_VALUE>',63) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES ( 64, 'JSON', 'Content-Type: application/json', '<DYNAMIC_PARAMETERS>', 'GET', 'com.bh.cp.proxy.handler.impl.DeviceNotUpdatedCounterResponseHandler', 'REST','https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cyberservice/v1/cyber/unsupportedswdevicecount',0,true ) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(100,'plantId','<FILTER_VALUE>',64) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES (66, 'JSON', 'Content-Type: application/json', '<DYNAMIC_PARAMETERS>', 'GET', 'com.bh.cp.proxy.handler.impl.ItemDetailsListResponseHandler', 'REST','https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cyberservice/v1/cyber/itemdetailslist',0,true ) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(102,'deviceId','<FILTER_VALUE>',66) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET widget_id=0 WHERE id= 37;

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES ( 67, 'JSON', 'Content-Type: application/json', '?vids=<APPLICABLE_MACHINE_VIDS_CSV>', 'GET', 'com.bh.cp.proxy.handler.impl.AxCoWwOptimizationSummaryResponseHandler', 'REST','https://mercurius.np-0000029.npaeuw1.bakerhughes.com/AxCoWW/v2/values',37,true ) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data='?vid=<APPLICABLE_MACHINE_VIDS_CSV>' WHERE widget_id=10;

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES ( 65, 'JSON', 'Content-Type: application/json', '<DYNAMIC_PARAMETERS>', 'GET', 'com.bh.cp.proxy.handler.impl.TotalDeviceCountResponse', 'REST','https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cyberservice/v1/cyber/devicecount',0,true ) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(101,'plantId','<FILTER_VALUE>',65) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES (68,'JSON','Content-Type: application/json','{"filterConditions":[<DYNAMIC_PARAMETERS>],"sortConditions":[{"field":"issue.serialno","order":"desc"}],"limit": null,"offset": "0"}','POST', 'com.bh.cp.proxy.handler.impl.CSASCaseHistoryResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases',68,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (103,'plantId','{"filterOperand1": {"field": "waf.plantid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',68), (104,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',68), (105,'endDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',68);

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES (69, 'JSON', 'Content-Type: application/json', '{<DYNAMIC_PARAMETERS>}', 'POST', 'com.bh.cp.proxy.handler.impl.PeriodFireTokenCountResponse', 'REST','https://mercurius.np-0000029.npaeuw1.bakerhughes.com/csasservice/v1/csas/plantfiredtokencount',0,true ) ON CONFLICT (id) DO NOTHING;
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(106,'fromDate','"fromDate":<FILTER_VALUE>',69) ON CONFLICT (id) DO NOTHING;
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(107,'toDate','"toDate":<FILTER_VALUE>',69) ON CONFLICT (id) DO NOTHING;
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(108,'plantId','"plantId":<FILTER_VALUE>',69) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id,communication_format,headers,input_data,method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES ( 71, 'JSON', 'Content-Type: application/json', '{<DYNAMIC_PARAMETERS>}', 'POST', 'com.bh.cp.proxy.handler.impl.LineUpFiredTokenCountResponse', 'REST','https://mercurius.np-0000029.npaeuw1.bakerhughes.com/csasservice/v1/csas/lineupfiredtokencount',0,true ) ON CONFLICT (id) DO NOTHING;
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(109,'fromDate','"fromDate":<FILTER_VALUE>',71) ON CONFLICT (id) DO NOTHING;
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(110,'toDate','"toDate":<FILTER_VALUE>',71) ON CONFLICT (id) DO NOTHING;
INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES(111,'lineupId','"lineupId":<FILTER_VALUE>',71) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) VALUES 
(72,'JSON','Content-Type: application/json','','GET','com.bh.cp.proxy.handler.impl.FiredTokenCountResponseHandler',
'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/csasservice/v1/csas/firedtokencount',72,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service) 
VALUES (73,'JSON','Content-Type: application/json','<DYNAMIC_PARAMETERS>','GET', 'com.bh.cp.proxy.handler.impl.KpiTokenResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/caseCsas',73,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id)
 VALUES (112,'caseId','caseId=<FILTER_VALUE>',73)ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)
 VALUES (74,'JSON','Content-Type: application/json', '{<DYNAMIC_PARAMETERS>}','POST', 'com.bh.cp.proxy.handler.impl.AddTokenResponseHandler', 
'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/csasservice/v1/csas/addtoken',74,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES (113,'issueId','"issueId":<FILTER_VALUE>',74),(114,'tokenQuantity','"tokenQuantity":<FILTER_VALUE>',74)  ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory(id, communication_format, headers, input_data, is_paid_service, method, output_handler, service_type, uri, widget_id)VALUES (76, 'JSON', 'Content-Type: application/json', '?vid=<ASSET_VID>&period=12M&kpiId=A', true, 'GET', 'com.bh.cp.proxy.handler.impl.KPIAvailabilityResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/icenter2pilot/kpi', 39) ON CONFLICT (id) DO NOTHING;
	
INSERT INTO services_directory(id, communication_format, headers, input_data, is_paid_service, method, output_handler, service_type, uri, widget_id)VALUES (75, 'JSON', 'Content-Type: application/json', '?vid=<ASSET_VID>&period=12M&kpiId=R', true, 'GET', 'com.bh.cp.proxy.handler.impl.KPIReliabilityResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/icenter2pilot/kpi', 40) ON CONFLICT (id) DO NOTHING;

UPDATE services_directory SET input_data = '{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field": "issue.csas"},"filterValues": ["Y"]}],"sortConditions":[{"field":"issue.serialno","order":"desc"}],"limit": "","offset": "0"}' WHERE ID=68;

UPDATE services_dynamic_parameters SET input_data = '{"filterOperand1": {"field": "issue.lineup_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}' WHERE ID=103 AND service_id=68;
 
UPDATE services_dynamic_parameters SET field ='lineupId' WHERE ID=103 AND service_id=68;

UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand":{"field":"<ASSET_LEVEL>"},"filterValues":["<ASSET_ID>"],"operation": "eq"}],"startDate":"<FROM_DT_YYYY_MM_DD_NQ>","endDate":"<TO_DT_YYYY_MM_DD_NQ>"}', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/trpservice/v1/KPI' WHERE widget_id =30;
UPDATE services_directory SET input_data='{"filterConditions":[{"filterOperand":{"field":"<ASSET_LEVEL>"},"filterValues":["<ASSET_ID>"],"operation": "eq"}],"startDate":"<FROM_DT_YYYY_MM_DD_NQ>","endDate":"<TO_DT_YYYY_MM_DD_NQ>"}', uri='https://mercurius.np-0000029.npaeuw1.bakerhughes.com/trpservice/v1/KPI' WHERE widget_id =31;

UPDATE services_directory SET input_data='?lineup_ids=<LINEUP_IDS_CSV>&modules=LINEUP&sections=LINEUP&calculations=FFH&outputs=FH&required_from=<FROM_DT_UTC_IN_FH>&required_to=<TO_DT_UTC_IN_FH>&outputs=FH' WHERE widget_id =26;

INSERT INTO services_directory (id, communication_format,headers,input_data,  method,output_handler,service_type,uri,widget_id,is_paid_service)VALUES (78,'JSON','Content-Type: application/json','{"filterConditions":[<DYNAMIC_PARAMETERS>,{"filterOperand1":{"field": "issue.flag_internal"},"filterValues": ["N"]}],"sortConditions":[{"field":"issue.event_date","order":"desc"}],"limit": null,"offset": "0"}','POST', 'com.bh.cp.proxy.handler.impl.CaseListDataResponseHandler', 'REST', 'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v5/cases',78,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES
(115,'projectId','{"filterOperand1": {"field": "waf.PROJECTID"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),
(116,'plantId','{"filterOperand1": {"field": "waf.plantid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),
(117,'trainId','{"filterOperand1": {"field": "waf.trainid"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),
(118,'lineupId','{"filterOperand1": {"field": "issue.lineup_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),
(119,'machineId','{"filterOperand1": {"field": "issue.serialno"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),
(120,'caseNumber','{"filterOperand1": {"field": "issue.issue_id"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),
(121,'criticality','{"filterOperand1": {"field": "issue.criticality"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),
(122,'customerPriority','{"filterOperand1": {"field": "issue.customer_priority"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),(123,'status','{"filterOperand1": {"field": "issue.status"},"filterValues": [<FILTER_VALUE>],"operation": "eq"}',78),
(124,'startDate','{"filterOperand1": {"field": "issue.event_date"},"filterValues": [<FILTER_VALUE>],"operation": "gt"}',78),
(125,'endDate','{"filterOperand1": {"field": "issue.issue_end_date"},"filterValues": [<FILTER_VALUE>],"operation": "lt"}',78) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_directory (id, communication_format,headers,input_data, method,
                                output_handler,service_type,
                                uri,widget_id,is_paid_service) VALUES
    (79,'JSON','Content-Type: application/json', '<DYNAMIC_PARAMETERS>','GET',
     'com.bh.cp.proxy.handler.impl.PeMSDataResponseHandler', 'REST',
     'https://mercurius.np-0000029.npaeuw1.bakerhughes.com/pems/v1/data',0,false) ON CONFLICT (id) DO NOTHING;

INSERT INTO services_dynamic_parameters( id,field,input_data,service_id) VALUES
(126,'sampling','sampling=<FILTER_VALUE>',79),
(127,'mode','mode=<FILTER_VALUE>',79),
(128,'vidList','vidList=<FILTER_VALUE>',79),
(129,'assetIdList','assetIdList=<FILTER_VALUE>',79),
(130,'from','from=<FILTER_VALUE>',79),
(131,'to','to=<FILTER_VALUE>',79) ON CONFLICT (id) DO NOTHING;