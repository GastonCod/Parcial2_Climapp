package com.istea.appdelclima.router
class MockRouter : Router { override fun navegar(ruta: String) = println("navegar a: $ruta") }
