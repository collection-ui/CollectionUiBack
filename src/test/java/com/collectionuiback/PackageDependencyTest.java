package com.collectionuiback;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

@AnalyzeClasses(packagesOf = CollectionUiBackApplication.class)
public class PackageDependencyTest {

    public static final String PACKAGE_BOILERPLATE = "..boilerplate..";
    public static final String PACKAGE_MODULE = "..module..";
    public static final String PACKAGE_INFRA = "..infra..";
    public static final String PACKAGE_ACCOUNT = "..module.account..";
    public static final String PACKAGE_EXCEPTION = "..module.exception..";
    public static final String PACKAGE_OAUTH = "..module.oauth..";

    @ArchTest
    ArchRule boilerplatePackageDependency = ArchRuleDefinition.noClasses()
            .that().resideInAPackage(PACKAGE_BOILERPLATE)
            .should().accessClassesThat().resideInAnyPackage(PACKAGE_MODULE, PACKAGE_INFRA);

    @ArchTest
    ArchRule infraPackageDependency = ArchRuleDefinition.noClasses()
            .that().resideInAPackage(PACKAGE_INFRA)
            .should().accessClassesThat().resideInAnyPackage(PACKAGE_MODULE, PACKAGE_BOILERPLATE);

    @ArchTest
    ArchRule cycleCheck = SlicesRuleDefinition.slices().matching("com.collectionuiback.module.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    ArchRule exceptionPackageRule = ArchRuleDefinition.classes()
            .that().resideInAPackage(PACKAGE_EXCEPTION)
            .should().dependOnClassesThat().resideInAPackage(PACKAGE_BOILERPLATE);

    @ArchTest
    ArchRule oauthPackageRule = ArchRuleDefinition.classes()
            .that().resideInAPackage(PACKAGE_OAUTH)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(PACKAGE_BOILERPLATE, PACKAGE_OAUTH, PACKAGE_EXCEPTION, PACKAGE_ACCOUNT);
}
