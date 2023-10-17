package ru.etu.graduatework.core.interactor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// базовый класс для прецедентов
abstract class UseCase<in Params, out Type> where Type : Any {
    // метод, в котором выполняется прецедент
    abstract suspend fun run(params: Params): Result<Type>
    // оператор вызова для удобного использования в коде
    operator fun invoke(
        params: Params,
        scope: CoroutineScope,
        onResult: (Result<Type>) -> Unit = {}
    ) {
        // запуск сопрограммы в контексте scope
        scope.launch(Dispatchers.Main) {
            val deferred = async(Dispatchers.IO) {
                run(params)
            }
            // ожидание выполнения прецедента
            // и вызов метода обратного вызова
            onResult(deferred.await())
        }
    }
}

// производный абстракнтый класс для прецедентов без параметров
abstract class UnitUseCase<out Type : Any> : UseCase<Unit, Type>() {
    operator fun invoke(
        scope: CoroutineScope,
        onResult: (Result<Type>) -> Unit = {}
    ) = invoke(Unit, scope, onResult)

    override suspend fun run(params: Unit) = run()

    protected abstract suspend fun run(): Result<Type>
}