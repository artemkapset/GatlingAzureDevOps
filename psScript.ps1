$url = "$(System.TeamFoundationCollectionUri)$(System.TeamProject)/_apis/build/builds/$(Build.TriggeredBy.BuildId)/timeline?api-version=5.1"
echo $url

$result = Invoke-RestMethod -Uri $url -Headers @{authorization = "Bearer $env:SYSTEM_ACCESSTOKEN"} -ContentType "application/json" -Method get

# filter the task's records by name
$taskResult = $result.records | where {$_.name -eq "GatlingLoad"}  

# calculate the totaltime of the newest build
$time = [datetime]$taskResult.finishTime - [datetime]$taskResult.startTime

$thisTaskTime= $time.TotalMinutes

# get the last build's Id
$lasturl = "$(System.TeamFoundationCollectionUri)$(System.TeamProject)/_apis/build/builds?definitions=$(Build.TriggeredBy.DefinitionId)&resultFilter=succeeded&`$top=2&api-version=5.1"

$buildResult =Invoke-RestMethod -Uri $lasturl -Headers @{authorization = "Bearer $env:SYSTEM_ACCESSTOKEN"} -ContentType "application/json" -Method get

#extract last buildId
$lastBuildId = $buildResult.value[1].id

#get the timeline of the last build
$timeUrl = "$(System.TeamFoundationCollectionUri)$(System.TeamProject)/_apis/build/builds/$($lastBuildId)/timeline?api-version=5.1"

$lastResult =Invoke-RestMethod -Uri $timeUrl -Headers @{authorization = "Bearer $env:SYSTEM_ACCESSTOKEN"} -ContentType "application/json" -Method get

#Caculate the totaltime of the last build task
# filter the task's records by name
$lastTaskResult = $lastResult.records | where {$_.name -eq "GatlingLoad"}  

$LastTime = [datetime]$lastTaskResult.finishTime - [datetime]$lastTaskResult.startTime

$lastTaskTime= $LastTime.TotalMinutes

#Store the result to varialbe  isLonger    
if($thisTaskTime -ge $lastTaskTime){ echo "##vso[task.setvariable variable=isLonger]True" }
