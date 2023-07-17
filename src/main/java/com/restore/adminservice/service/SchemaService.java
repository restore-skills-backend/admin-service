package com.restore.adminservice.service;

import com.restore.core.exception.RestoreSkillsException;

public interface SchemaService {

    void createSchemaAndTables(String schemaName) throws RestoreSkillsException;
}
