package ru.art.platform.service

import ru.art.platform.constants.MigrationStatus.PROCESSED
import ru.art.platform.constants.Migrations.USER_RIGHTS_MIGRATION
import ru.art.platform.repository.MigrationRepository.getMigrationStatus
import ru.art.platform.repository.MigrationRepository.putMigrationStatus
import ru.art.platform.repository.UserRepository.getUsers
import ru.art.platform.repository.UserRepository.putUser
import ru.art.platform.service.UserService.getInitialUserActions

object MigrationService {

}
