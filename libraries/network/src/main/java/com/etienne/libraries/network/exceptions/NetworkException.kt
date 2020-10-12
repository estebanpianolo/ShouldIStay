package com.etienne.libraries.network.exceptions

import java.io.IOException

class NetworkException(val originalException: IOException) : IOException()
