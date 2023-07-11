import React, {useState} from 'react';
import {Widget, WidgetState} from "../../framework/widgets/Widget";
import {useProjectApi} from "../../api/ProjectApi";
import {subscribe} from "../../framework/pattern/Subscribe";
import {deactivateStreams, subscribeOnStreams} from "../../streams/Streams";
import {DELETE_EVENT} from "../../constants/EventTypes";
import {StreamEvent} from "../../framework/pattern/Stream";
import {empty} from "../../framework/dsl/simple/SimpleEmptyComponent";
import {useNotifications} from "../../framework/hooks/Hooks";
import {hookContainer} from "../../framework/pattern/HookContainer";
import {useAssemblyApi} from "../../api/AssemblyApi";
import {useLoadTestingApi} from "../../api/LoadTestingApi";
import {useModuleApi} from "../../api/ModuleApi";
import {activateProjectStream, projectStream} from "../../streams/ProjectStream";
import {Assembly} from "../../model/AssemblyTypes";
import {activateModuleStream, moduleStream} from "../../streams/ModuleStream";
import {activateLoadTestStream, loadTestStream} from "../../streams/LoadTestStream";
import {activateAssemblyStream, assemblyStream} from "../../streams/AssemblyStream";
import {LoadTest} from "../../model/LoadTestingTypes";
import {Module} from "../../model/ModuleTypes";
import {observe} from "../../framework/pattern/Observable";
import {activateUserStream, onUserUpdated, userStream} from "../../streams/UserStream";
import {useUserApi} from "../../api/UserApi";
import {platform} from "../entry/EntryPoint";
import {Project} from "../../model/ProjectTypes";
import {doNothing} from "../../framework/constants/Constants";

class StreamsActivator extends Widget<StreamsActivator> {
    #widget: Widget<any>;
    #hooks = hookContainer();
    #projectApi = this.#hooks.hookValue(useProjectApi);
    #assemblyApi = this.#hooks.hookValue(useAssemblyApi);
    #loadTestingApi = this.#hooks.hookValue(useLoadTestingApi);
    #moduleApi = this.#hooks.hookValue(useModuleApi);
    #userApi = this.#hooks.hookValue(useUserApi);

    #produceProjectEvent = (event: StreamEvent<Project>) => {
        const userId = platform.user()?.id;
        if (userId == undefined) {
            return;
        }

        this.#userApi().getUser(userId, user => {
            if (user.admin || user.availableProjects.includes(event.data.id)) {
                projectStream.produceEvent(event)
            }
        })
    };

    #produceAssemblyEvent = (event: StreamEvent<Assembly>) => {
        if (event.type == DELETE_EVENT) {
            assemblyStream.produceEvent(event);
            return;
        }
        this.#projectApi().getProject(event.data.projectId, project => assemblyStream.produceEvent({
            ...event,
            data: {
                ...event.data,
                projectName: project.name
            }
        }))
    };

    #produceLoadTestEvent = (event: StreamEvent<LoadTest>) => {
        if (event.type == DELETE_EVENT) {
            loadTestStream.produceEvent(event);
            return;
        }
        this.#projectApi().getProject(event.data.projectId, project => loadTestStream.produceEvent({
            ...event,
            data: {
                ...event.data,
                projectName: project.name
            }
        }))
    };

    #produceModuleEvent = (event: StreamEvent<Module>) => {
        if (event.type == DELETE_EVENT) {
            moduleStream.produceEvent(event);
            return;
        }
        this.#projectApi().getProject(event.data.projectId, project => moduleStream.produceEvent({
            ...event,
            data: {
                ...event.data,
                projectName: project.name
            }
        }));

    };

    constructor(widget: Widget<any>) {
        super();
        this.#widget = widget;
    }

    draw = () => {
        this.#hooks.evaluate();

        const [initialized, setInitialized] = useState(false);
        const notifications = useNotifications();

        subscribe(() => {
            if (initialized) {
                return deactivateStreams;
            }
            activateProjectStream(this.#projectApi().subscribeOnProject(event => this.state != WidgetState.UNMOUNTED && this.#produceProjectEvent(event)));
            activateAssemblyStream(this.#assemblyApi().subscribeOnAssembly(event => this.state != WidgetState.UNMOUNTED && this.#produceAssemblyEvent(event)));
            activateLoadTestStream(this.#loadTestingApi().subscribeOnLoadTest(event => this.state != WidgetState.UNMOUNTED && this.#produceLoadTestEvent(event)));
            activateModuleStream(this.#moduleApi().subscribeOnModule(event => this.state != WidgetState.UNMOUNTED && this.#produceModuleEvent(event)));
            activateUserStream(this.#userApi().subscribeOnUser(event => this.state != WidgetState.UNMOUNTED && userStream.produceEvent(event)));
            subscribeOnStreams(notifications);
            const disposeUserStream = onUserUpdated(platform.user().id, platform.setUser);
            setInitialized(true);
            return () => {
                disposeUserStream()
                deactivateStreams()
                setInitialized(false)
            }
        });

        return observe(initialized).render(() => initialized ? this.#widget.render() : empty().render());
    }
}

export const streamsActivator = (component: Widget<any>) => new StreamsActivator(component);
