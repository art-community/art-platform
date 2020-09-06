package ru.art.platform.panel.repository

import ru.art.platform.api.mapping.load.LoadTestMapper.fromLoadTest
import ru.art.platform.api.mapping.load.LoadTestMapper.toLoadTest
import ru.art.platform.api.mapping.load.LoadTestScenarioMapper.fromLoadTestScenario
import ru.art.platform.api.mapping.load.LoadTestScenarioMapper.toLoadTestScenario
import ru.art.platform.api.model.load.LoadTest
import ru.art.platform.api.model.load.LoadTestScenario
import ru.art.platform.common.constants.ErrorCodes.LOAD_TEST_DOES_NOT_EXISTS
import ru.art.platform.common.constants.ErrorCodes.LOAD_TEST_SCENARIO_DOES_NOT_EXISTS
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.constants.DbConstants
import ru.art.platform.panel.constants.DbConstants.LOAD_TEST_SCENARIO_PROJECT_ID_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.PROJECT_ID_INDEX_NAME
import ru.art.tarantool.dao.TarantoolDao.tarantool
import java.util.*

object LoadTestingRepository {
    fun putLoadTest(newTest: LoadTest): LoadTest = toLoadTest.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(LOAD_TEST_CAMEL_CASE, fromLoadTest.map(newTest)))

    fun putLoadTestScenario(scenario: LoadTestScenario): LoadTestScenario = toLoadTestScenario.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(LOAD_TEST_SCENARIO_CAMEL_CASE, fromLoadTestScenario.map(scenario)))

    fun getLoadTest(id: Long): LoadTest = tryGetLoadTest(id)
            .orElseThrow { PlatformException(LOAD_TEST_DOES_NOT_EXISTS, "Load test with an id '$id' does not exist") }

    fun tryGetLoadTest(id: Long): Optional<LoadTest> = tarantool(PLATFORM_CAMEL_CASE)
            .get(LOAD_TEST_CAMEL_CASE, setOf(id))
            .map(toLoadTest::map)

    fun getLoadTestScenario(id: Long): LoadTestScenario = tryGetLoadTestScenario(id)
            .orElseThrow { PlatformException(LOAD_TEST_SCENARIO_DOES_NOT_EXISTS, "Load test scenario with an id '$id' does not exist") }

    fun getProjectsLoadTestScenarios(projectId: Long): List<LoadTestScenario> = tarantool(PLATFORM_CAMEL_CASE)
            .selectByIndex(LOAD_TEST_SCENARIO_CAMEL_CASE, PROJECT_ID_INDEX_NAME, setOf(projectId))
            .map(toLoadTestScenario::map)

    fun getProjectsLoadTests(projectId: Long): List<LoadTest> = tarantool(PLATFORM_CAMEL_CASE)
            .selectByIndex(LOAD_TEST_CAMEL_CASE, PROJECT_ID_INDEX_NAME, setOf(projectId))
            .map(toLoadTest::map)

    fun tryGetLoadTestScenario(id: Long): Optional<LoadTestScenario> = tarantool(PLATFORM_CAMEL_CASE)
            .get(LOAD_TEST_SCENARIO_CAMEL_CASE, setOf(id))
            .map(toLoadTestScenario::map)

    fun getLoadTests(): List<LoadTest> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(LOAD_TEST_CAMEL_CASE)
            .map(toLoadTest::map)

    fun deleteLoadTest(id: Long): LoadTest = tarantool(PLATFORM_CAMEL_CASE)
            .delete(LOAD_TEST_CAMEL_CASE, id)
            .map(toLoadTest::map)
            .orElseThrow { PlatformException(LOAD_TEST_DOES_NOT_EXISTS, "Load test with an id '$id' does not exist") }

    fun deleteLoadTestScenario(id: Long): LoadTestScenario = tarantool(PLATFORM_CAMEL_CASE)
            .delete(LOAD_TEST_SCENARIO_CAMEL_CASE, id)
            .map(toLoadTestScenario::map)
            .orElseThrow { PlatformException(LOAD_TEST_DOES_NOT_EXISTS, "Load scenario test with an id '$id' does not exist") }
}
