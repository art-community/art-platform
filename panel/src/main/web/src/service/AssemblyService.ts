import {ASSEMBLY_BUILDING_STATE, ASSEMBLY_RESTARTED_STATE, ASSEMBLY_STARTED_ON_RESOURCE_STATE} from "../constants/States";
import {Assembly, AssemblyInformation} from "../model/AssemblyTypes";

export const assemblyIsRunning = (assembly?: AssemblyInformation | Assembly) => {
    if (!assembly) {
        return false;
    }
    return assembly.state == ASSEMBLY_BUILDING_STATE || assembly.state == ASSEMBLY_RESTARTED_STATE || assembly.state == ASSEMBLY_STARTED_ON_RESOURCE_STATE;
}
