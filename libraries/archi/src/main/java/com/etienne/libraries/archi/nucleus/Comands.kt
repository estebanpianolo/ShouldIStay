package com.etienne.libraries.archi.nucleus

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.cast

interface Command {
    fun execute(): Single<Action>
}

object NoCommand : Command {
    override fun execute(): Single<Action> = Single.never()
}

fun simpleCommand(nextAction: Action = DoNothing, function: () -> Unit) = object : Command {
    override fun execute(): Single<Action> = Single.fromCallable {
        function()
        nextAction
    }
}

fun <A : Action> Single<A>.asCommand(): Command {
    return object : Command {
        override fun execute(): Single<Action> {
            return this@asCommand.cast()
        }
    }
}
