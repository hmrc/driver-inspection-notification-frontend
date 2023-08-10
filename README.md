
# driver-inspection-notification-frontend

This public-facing, frontend microservice is part of the Goods Vehicle Movement System (GVMS) suite of services. It provides a means of notifying drivers post-embarkation if the goods they are carrying are cleared to proceed or require further checks.

## Running the service locally
By default, the service runs on 9004 port.

#### Using sbt
For local development use `sbt run`.

This is an unauthenticated service

Navigate to http://localhost:9004/driver-inspection-notification/start" 

```GET        /driver-inspection-notification/start ```

Loads up start screen with additional information

> Response Status: 200 

```GET        /driver-inspection-notification/search ```

Loads up search screen with input parameter of GMRID

> Response Status: 200

```POST       /driver-inspection-notification/search ```

Submit a Gmr Id to goods-movement-system to search for the record and its inspection status

> Response Status: 303
    Redirects to outcome page. 

> Response Status: 400
    Used for several scenarios: 
        1. GMRID not in the correct format
        2. GMRID field no supplied
        3. GMRID does not exist in goods-movement-system database.

``` GET        /driver-inspection-notification/result/inspection-required ```
Loads up information about a GMR that requires inspection.

This state can change and should be checked before leaving the ship/train.

> Response Status: 200

```GET        /driver-inspection-notification/result/inspection-not-needed```
Loads up information about a GMR that does not require inspection, 

This state can change and should be checked before leaving the ship/train. 

> Response Status: 200

```GET        /driver-inspection-notification/result/inspection-pending```
Loads up information when the inspection status has not concluded yet, this state means we are still awaiting information from HOD systems. 

This state can change and should be checked before leaving the ship/train

> Response Status: 200