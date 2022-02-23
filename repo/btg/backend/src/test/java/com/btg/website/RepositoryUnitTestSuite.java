package com.btg.website;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@SelectPackages({"com.btg.website.repository"})
@Suite
public class RepositoryUnitTestSuite {
}
