### Solution Description
See task description below
This solution consists of 4 services.

1. Viewing reservation service
PUT /flat/viewing-slot/2019-11-12/10-00?tenantId=1

2. Viewing cancelling service
DELETE /flat/viewing-slot/2019-11-12/10-00?tenantId=1

3. Approving service
PUT /flat/viewing-slot/2019-11-12/10-00/approve

4. Rejecting service
PUT /flat/viewing-slot/2019-11-12/10-00/reject


### Backend task description

Develop a flat viewing scheduler.

There are 2 parties to the service:
New Tenants who are moving in
Current Tenant who lives in a flat

#### Service requirements:
1. Flat has 20-minute viewing slots from 10:00 to 20:00 for the upcoming week
2. New Tenant should be able to reserve viewing. Once reserved slot can not be occupied by other tenants.
3. Current Tenant should be notified about reservation in at least 24 hours and can either approve or reject it. Once rejected this slot can not be reserved by anyone else at any point.
4. New Tenant gets notified about approval or rejection.
5. New Tenant can cancel the viewing at any point, current tenant should be notified and viewing slot becomes vacant.

#### Constraints:
Kotlin/Java as a language
Make service available over HTTP
Use minimal approach to the task, no need to use DB/authentication
Please don’t use big frameworks such as Spring(boot)
Use stubs for notifications

#### Evaluation:
Expecting to see feature complete, clean and tested code



