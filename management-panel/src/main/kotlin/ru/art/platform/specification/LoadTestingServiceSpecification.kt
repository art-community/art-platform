package ru.art.platform.specification

import ru.art.entity.CollectionMapping.collectionValueFromModel
import ru.art.entity.PrimitiveMapping.longMapper
import ru.art.platform.api.mapping.load.LoadTestMapper.fromLoadTest
import ru.art.platform.api.mapping.load.LoadTestScenarioMapper.fromLoadTestScenario
import ru.art.platform.api.mapping.load.LoadTestScenarioMapper.toLoadTestScenario
import ru.art.platform.api.mapping.request.LoadTestRequestMapper.toLoadTestRequest
import ru.art.platform.api.mapping.request.LoadTestScenarioRequestMapper.toLoadTestScenarioRequest
import ru.art.platform.common.broker.PlatformEventMapper
import ru.art.platform.constants.ServiceConstants.CANCEL_LOAD_TEST
import ru.art.platform.constants.ServiceConstants.DELETE_LOAD_TEST
import ru.art.platform.constants.ServiceConstants.DELETE_LOAD_TEST_SCENARIO
import ru.art.platform.constants.ServiceConstants.GET_LOAD_TEST
import ru.art.platform.constants.ServiceConstants.GET_LOAD_TEST_SCENARIO
import ru.art.platform.constants.ServiceConstants.GET_PROJECTS_LOAD_TESTS
import ru.art.platform.constants.ServiceConstants.GET_PROJECTS_LOAD_TEST_SCENARIOS
import ru.art.platform.constants.ServiceConstants.SAVE_LOAD_TEST_SCENARIO
import ru.art.platform.constants.ServiceConstants.START_LOAD_TEST
import ru.art.platform.constants.ServiceConstants.SUBSCRIBE_ON_LOAD_TEST
import ru.art.platform.constants.ServiceConstants.UPDATE_LOAD_TEST_SCENARIO
import ru.art.platform.service.LoadTestingService
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket
import ru.art.service.constants.RequestValidationPolicy.NOT_NULL
import ru.art.service.constants.RequestValidationPolicy.VALIDATABLE

fun registerLoadTestingService() {
    rsocket(START_LOAD_TEST)
            .requestMapper(toLoadTestRequest)
            .responseMapper(fromLoadTest)
            .validationPolicy(VALIDATABLE)
            .handle(LoadTestingService::startLoadTest)
    rsocket(CANCEL_LOAD_TEST)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromLoadTest)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::cancelLoadTest)
    rsocket(GET_LOAD_TEST)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromLoadTest)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::getLoadTest)
    rsocket(SAVE_LOAD_TEST_SCENARIO)
            .requestMapper(toLoadTestScenarioRequest)
            .responseMapper(fromLoadTestScenario)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::saveLoadTestScenario)
    rsocket(UPDATE_LOAD_TEST_SCENARIO)
            .requestMapper(toLoadTestScenario)
            .responseMapper(fromLoadTestScenario)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::updateLoadTestScenario)
    rsocket(GET_LOAD_TEST_SCENARIO)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromLoadTestScenario)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::getLoadTestScenario)
    rsocket(GET_PROJECTS_LOAD_TEST_SCENARIOS)
            .requestMapper(longMapper.toModel)
            .responseMapper(collectionValueFromModel(fromLoadTestScenario)::map)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::getProjectsLoadTestScenarios)
    rsocket(GET_PROJECTS_LOAD_TESTS)
            .requestMapper(longMapper.toModel)
            .responseMapper(collectionValueFromModel(fromLoadTest)::map)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::getProjectsLoadTests)
    rsocket(DELETE_LOAD_TEST_SCENARIO)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromLoadTestScenario)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::deleteLoadTestScenario)
    rsocket(SUBSCRIBE_ON_LOAD_TEST)
            .responseMapper(PlatformEventMapper.fromPlatformEvent)
            .responseProcessingMode(REACTIVE)
            .produce(LoadTestingService::subscribeOnLoadTest)
    rsocket(DELETE_LOAD_TEST)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromLoadTest)
            .validationPolicy(NOT_NULL)
            .handle(LoadTestingService::deleteLoadTest)
}