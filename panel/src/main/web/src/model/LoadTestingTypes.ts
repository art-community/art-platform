import {Variable} from "./Variable";
import {GradleAssemblyConfiguration} from "./GradleTypes";
import {ResourceIdentifier} from "./ResourceTypes";
import {ProjectVersion} from "./ProjectTypes";
import {PlatformFileIdentifier} from "./PlatformFileTypes";

export type LoadTestRequest = {
    scenarioId: number;
    resourceId: ResourceIdentifier;
    projectId: number;
    version: ProjectVersion;
    environmentVariables: Variable[];
}

export type LoadTest = {
    id: number
    scenarioId: number;
    projectId: number;
    version: ProjectVersion;
    startTimeStamp: number;
    endTimeStamp: number;
    state: string;
    resourceId: ResourceIdentifier;
    logId: number;
    reportArchiveName: PlatformFileIdentifier;
    environmentVariables: Variable[];
}

export type LoadTestScenarioRequest = {
    name: string;
    defaultResourceId: ResourceIdentifier;
    projectId: number;
    launchTechnology: string;
    reportTechnology: string;
    gradleConfiguration: GradleAssemblyConfiguration;
}

export type LoadTestScenario = {
    id: number
    name: string;
    defaultResourceId: ResourceIdentifier;
    projectId: number;
    launchTechnology: string;
    reportTechnology: string;
    gradleConfiguration: GradleAssemblyConfiguration;
}
