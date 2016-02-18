package ru.netimen.hitch_hikingstats

import rx.Observable
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   18.02.16
 */

//Error:(24, 9) org.jetbrains.kotlin.codegen.CompilationException: Back-end (JVM) Internal error: Error type encountered: org.jetbrains.kotlin.types.ErrorUtils$UninferredParameterTypeConstructor@68754e6 (ErrorTypeImpl).
//Cause: Error type encountered: org.jetbrains.kotlin.types.ErrorUtils$UninferredParameterTypeConstructor@68754e6 (ErrorTypeImpl).
//File being compiled and position: (24,9) in /home/d/work/workspace/Hitch-hiking-Stats/app/src/main/java/ru/netimen/hitch_hikingstats/MemoryRepo.kt
//PsiElement: val resultObservable = Observable.just(dangerousOperation()).compose(wrapResult(::getErrorMessage))
//The root cause was thrown at: JetTypeMapper.java:435
//	at org.jetbrains.kotlin.codegen.ExpressionCodegen.genQualified(ExpressionCodegen.java:299)
//	at org.jetbrains.kotlin.codegen.ExpressionCodegen.genStatement(ExpressionCodegen.java:339)
//	at org.jetbrains.kotlin.codegen.ExpressionCodegen.generateBlock(ExpressionCodegen.java:1532)
//	at org.jetbrains.kotlin.codegen.ExpressionCodegen.generateBlock(ExpressionCodegen.java:1485)
//	at org.jetbrains.kotlin.codegen.CodegenStatementVisitor.visitBlockExpression(CodegenStatementVisitor.java:56)
//	at org.jetbrains.kotlin.codegen.CodegenStatementVisitor.visitBlockExpression(CodegenStatementVisitor.java:22)
//	at org.jetbrains.kotlin.psi.KtBlockExpression.accept(KtBlockExpression.java:44)
//	at org.jetbrains.kotlin.codegen.ExpressionCodegen.genQualified(ExpressionCodegen.java:280)
//	at org.jetbrains.kotlin.codegen.ExpressionCodegen.genStatement(ExpressionCodegen.java:339)
//	at org.jetbrains.kotlin.codegen.ExpressionCodegen.gen(ExpressionCodegen.java:309)
//	at org.jetbrains.kotlin.codegen.ExpressionCodegen.returnExpression(ExpressionCodegen.java:1873)
//	at org.jetbrains.kotlin.codegen.FunctionGenerationStrategy$FunctionDefault.doGenerateBody(FunctionGenerationStrategy.java:50)
//	at org.jetbrains.kotlin.codegen.FunctionGenerationStrategy$CodegenBased.generateBody(FunctionGenerationStrategy.java:72)
//	at org.jetbrains.kotlin.codegen.FunctionCodegen.generateMethodBody(FunctionCodegen.java:364)
//	at org.jetbrains.kotlin.codegen.FunctionCodegen.generateMethod(FunctionCodegen.java:203)
//	at org.jetbrains.kotlin.codegen.FunctionCodegen.generateMethod(FunctionCodegen.java:138)
//	at org.jetbrains.kotlin.codegen.FunctionCodegen.gen(FunctionCodegen.java:113)
//	at org.jetbrains.kotlin.codegen.MemberCodegen.genFunctionOrProperty(MemberCodegen.java:180)
//	at org.jetbrains.kotlin.codegen.ClassBodyCodegen.generateDeclaration(ClassBodyCodegen.java:124)
//	at org.jetbrains.kotlin.codegen.ClassBodyCodegen.generateBody(ClassBodyCodegen.java:74)
//	at org.jetbrains.kotlin.codegen.MemberCodegen.generate(MemberCodegen.java:117)
//	at org.jetbrains.kotlin.codegen.MemberCodegen.genClassOrObject(MemberCodegen.java:231)
//	at org.jetbrains.kotlin.codegen.PackageCodegen.generateClassOrObject(PackageCodegen.java:147)
//	at org.jetbrains.kotlin.codegen.PackageCodegen.generateFile(PackageCodegen.java:98)
//	at org.jetbrains.kotlin.codegen.PackageCodegen.generate(PackageCodegen.java:61)
//	at org.jetbrains.kotlin.codegen.KotlinCodegenFacade.generatePackage(KotlinCodegenFacade.java:99)
//	at org.jetbrains.kotlin.codegen.KotlinCodegenFacade.doGenerateFiles(KotlinCodegenFacade.java:77)
//	at org.jetbrains.kotlin.codegen.KotlinCodegenFacade.compileCorrectFiles(KotlinCodegenFacade.java:44)
//	at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.generate(KotlinToJVMBytecodeCompiler.kt:376)
//	at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.analyzeAndGenerate(KotlinToJVMBytecodeCompiler.kt:275)
//	at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.compileBunchOfSources(KotlinToJVMBytecodeCompiler.kt:194)
//	at org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.kt:194)
//	at org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.kt:49)
//	at org.jetbrains.kotlin.cli.common.CLICompiler.exec(CLICompiler.java:174)
//	at org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile.callCompiler(Tasks.kt:85)
//	at org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile.compile(Tasks.kt:61)
//	at sun.reflect.GeneratedMethodAccessor445.invoke(Unknown Source)
//	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//	at java.lang.reflect.Method.invoke(Method.java:498)
//	at org.gradle.internal.reflect.JavaMethod.invoke(JavaMethod.java:75)
//	at org.gradle.api.internal.project.taskfactory.AnnotationProcessingTaskFactory$StandardTaskAction.doExecute(AnnotationProcessingTaskFactory.java:227)
//	at org.gradle.api.internal.project.taskfactory.AnnotationProcessingTaskFactory$StandardTaskAction.execute(AnnotationProcessingTaskFactory.java:220)
//	at org.gradle.api.internal.project.taskfactory.AnnotationProcessingTaskFactory$StandardTaskAction.execute(AnnotationProcessingTaskFactory.java:209)
//	at org.gradle.api.internal.AbstractTask$TaskActionWrapper.execute(AbstractTask.java:585)
//	at org.gradle.api.internal.AbstractTask$TaskActionWrapper.execute(AbstractTask.java:568)
//	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeAction(ExecuteActionsTaskExecuter.java:80)
//	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeActions(ExecuteActionsTaskExecuter.java:61)
//	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.execute(ExecuteActionsTaskExecuter.java:46)
//	at org.gradle.api.internal.tasks.execution.PostExecutionAnalysisTaskExecuter.execute(PostExecutionAnalysisTaskExecuter.java:35)
//	at org.gradle.api.internal.tasks.execution.SkipUpToDateTaskExecuter.execute(SkipUpToDateTaskExecuter.java:64)
//	at org.gradle.api.internal.tasks.execution.ValidatingTaskExecuter.execute(ValidatingTaskExecuter.java:58)
//	at org.gradle.api.internal.tasks.execution.SkipEmptySourceFilesTaskExecuter.execute(SkipEmptySourceFilesTaskExecuter.java:52)
//	at org.gradle.api.internal.tasks.execution.SkipTaskWithNoActionsExecuter.execute(SkipTaskWithNoActionsExecuter.java:52)
//	at org.gradle.api.internal.tasks.execution.SkipOnlyIfTaskExecuter.execute(SkipOnlyIfTaskExecuter.java:53)
//	at org.gradle.api.internal.tasks.execution.ExecuteAtMostOnceTaskExecuter.execute(ExecuteAtMostOnceTaskExecuter.java:43)
//	at org.gradle.execution.taskgraph.DefaultTaskGraphExecuter$EventFiringTaskWorker.execute(DefaultTaskGraphExecuter.java:203)
//	at org.gradle.execution.taskgraph.DefaultTaskGraphExecuter$EventFiringTaskWorker.execute(DefaultTaskGraphExecuter.java:185)
//	at org.gradle.execution.taskgraph.AbstractTaskPlanExecutor$TaskExecutorWorker.processTask(AbstractTaskPlanExecutor.java:62)
//	at org.gradle.execution.taskgraph.AbstractTaskPlanExecutor$TaskExecutorWorker.run(AbstractTaskPlanExecutor.java:50)

fun dangerousOperation() = 0

fun getErrorMessage(t: Throwable) = "error occurred"
class A {
    class Result<T, E>(val data: T? = null, val error: E? = null)

    fun <T, E> wrapResult(errorInfoFactory: (Throwable) -> E): (Observable<T>) -> Observable<Result<T, E>> = { it.map { Result<T, E>(it) }.onErrorReturn { Result<T, E>(error = errorInfoFactory(it)) } }


    fun test() {
        val resultObservable = Observable.fromCallable(::dangerousOperation).compose(wrapResult<Int, String>(::getErrorMessage))
    }

}

class MemoryRidesRepo : RidesRepo {
    private val rides = HashSet<Ride>()

    override fun getList(query: Repo.Query<TripListParams>): Observable<Result<List<Ride>, ErrorInfo>> = Observable.just(rides.filter { it.trip == query.listParams.trip }).wrapResult { ErrorInfo() }

    override fun get(id: String): Observable<Result<Ride, ErrorInfo>> = throw UnsupportedOperationException()

    override fun addOrUpdate(t: Ride) {
        rides.add(t)
    }

    override fun remove(t: Ride) {
        rides.remove(t)
    }

}


