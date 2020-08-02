import {Notifications} from "../framework/extensions/Notifications";
import {deactivateProjectStream, subscribeOnProjects} from "./ProjectStream";
import {deactivateAssemblyStream, subscribeOnAssembly} from "./AssemblyStream";
import {deactivateLogStream} from "./LogStream";
import {deactivateLoadTestStream, subscribeOnLoadTest} from "./LoadTestStream";
import {deactivateModuleStream, subscribeOnModule} from "./ModuleStream";
import {deactivateUserStream, subscribeOnUsers} from "./UserStream";

export const deactivateStreams = () => {
    deactivateProjectStream();
    deactivateAssemblyStream();
    deactivateLogStream();
    deactivateLoadTestStream();
    deactivateModuleStream();
    deactivateUserStream();
};

export const subscribeOnStreams = (notifications: Notifications) => {
    subscribeOnProjects(notifications);
    subscribeOnAssembly(notifications);
    subscribeOnLoadTest(notifications);
    subscribeOnModule(notifications);
    subscribeOnUsers(notifications);
};
