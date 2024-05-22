[![BH-INTERNAL-COPY-LEFT](https://img.shields.io/badge/license-BH--INTERNAL--COPY--LEFT-018374)](LICENSE.md)

# Dashboard Application

The Dashboard Application will be the landing application after User login. 
The User can change time period and asset, these changes will cause widget data refresh, based on new asset and time.
The User can configure the Dashboard and save the configuration to land on that dashboard always after login (the favorite dashboard).
The Dashboard is defined on 5 levels: Projects, Plants, Trains, Lineups and Machines. 
Widgets in the Aggregator section can be related to one, two or all levels. This relationship is defined in the App back-end
When the user save the favorite Dashboard Configuration, he/she can select the landing level (project, train, lineup or machine). 
The default level is the Project.

## Proxy Data Services

Proxy Data Services provides the APIs for getting formatted data from REST services.

## Version

1.0.0

### User Stories 
Sprint | User-Story | Name
------ | ---------- | ----------------------------------------
1 | US448872 | SPARQ Components setup : SS Adapter
2 | US450875 | Case Management Widget : iCenter cases in open status
2 | US450876 | Case Management Widget : iCenter cases per criticality
2 | US450877 | Case Management Widget : iCenter cases
2 | US450878 | Case Management Widget :  Last iCenter case
2 | US450879 | Database design : Entity Creation
2 | US450880 | TRP Cases Widget : Trip Reduction Program
2 | US450884 | KPIs : Fired Hours, Total Starts, Total trips
2 | US450893 | Dashboard : Project Dashboard
2 | US451413 | Test cases  on Asset Hierarchy, Dashboard & User management(Version1)
3 | US452889 | KPI : MTBT & Starting Reliability - Project dashboard
3 | US452890 | KPI : Fired Hours, Total Starts, Total trips for Plant dashboard
3 | US452894 | Project Dashboard
4 | US455363 | MTBT and starting reliability API response changes
4 | US455381 | KPI for Open and Closed cases
4 | US455564 | Display the no data found / service down message when APIs does not return any value
4 | US455725 | Bug Fixes
4 | US456279 | Technical Debts
5 | US459332 | Case Management Widget Enhancements :  iCenter cases in open status
6 | US455357 | Health Index Widget
6 | US459333 | Case Management widget enhancements: iCenter cases per criticality
6 | US459336 | Case management widget enhancements : Last iCenter case
6 | US461045 | TIL Implementation Status Widget  POC
6 | US461046 | Thrust Bearing Load Widget POC
6 | US461047 | Thrust Bearing Load 7 B Widget POC
6 | US461052 | LineUp GT AxCo Widget POC
6 | US461053 | CeCo Operating Profile Widget POC
6 | US461055 | 4 B thrust Bearing Sumary Widget POC
6 | US461056 | 7 B thrust Bearing Sumary WIdget POC
6 | US461057 | Training Widget WIdget POC
6 | US461058 | Spinning Reverse Summary Widget POC
6 | US461595 | Filter change advisory widget poc
7 | US461049 | Maintanance Optimizer Widget POC
7 | US461054 | CeCo map Widget POC
9 | US466505 | GT DLE health status - Response Handler
