package com.bytestream

import io.micronaut.runtime.Micronaut


fun main(args: Array<String>) {
    Micronaut.build().eagerInitConfiguration(true).eagerInitSingletons(true).args(*args).start()
}

