package xyz.cuteclouds.earthchan.utils.helpers

import xyz.cuteclouds.earthchan.extensions.newTask
import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean
import java.util.concurrent.TimeUnit

object AsyncInfoMonitor {
    var availableProcessors = Runtime.getRuntime().availableProcessors()
        private set

    var cpuUsage = 0.0
        private set

    var freeMemory = 0.0
        private set

    private val gb = (1024 * 1024 * 1024).toDouble()
    private var lastProcessCpuTime = 0.0
    private var lastSystemTime: Long = 0
    var maxMemory = 0.0
        private set

    var threadCount = 0
        private set

    var totalMemory = 0.0
        private set

    var vpsCPUUsage = 0.0
        private set

    var vpsFreeMemory = 0.0
        private set

    var vpsMaxMemory = 0.0
        private set

    var vpsUsedMemory = 0.0
        private set

    init {
        val os = ManagementFactory.getOperatingSystemMXBean()
        val thread = ManagementFactory.getThreadMXBean()
        val r = Runtime.getRuntime()
        val mb = 0x100000

        lastSystemTime = System.nanoTime()
        lastProcessCpuTime = calculateProcessCpuTime(os)

        newTask("AsyncInfoMonitorThread", 1, TimeUnit.SECONDS) {
            threadCount = thread.threadCount
            availableProcessors = r.availableProcessors()
            freeMemory = (Runtime.getRuntime().freeMemory() / mb).toDouble().floor(0.2)
            maxMemory = (Runtime.getRuntime().maxMemory() / mb).toDouble().floor(0.2)
            totalMemory = (Runtime.getRuntime().totalMemory() / mb).toDouble().floor(0.2)
            cpuUsage = calculateCpuUsage(os).floor(100.0)
            vpsCPUUsage = getVpsCPUUsage(os).floor(100.0)
            vpsFreeMemory = calculateVPSFreeMemory(os).floor(100.0)
            vpsMaxMemory = calculateVPSMaxMemory(os).floor(100.0)
            vpsUsedMemory = (vpsMaxMemory - vpsFreeMemory).floor(100.0)
        }
    }

    private fun calculateCpuUsage(os: OperatingSystemMXBean): Double {
        val systemTime = System.nanoTime()
        val processCpuTime = calculateProcessCpuTime(os)

        val cpuUsage = (processCpuTime - lastProcessCpuTime) / (systemTime - lastSystemTime).toDouble()

        lastSystemTime = systemTime
        lastProcessCpuTime = processCpuTime

        return cpuUsage / availableProcessors
    }

    private fun calculateProcessCpuTime(os: OperatingSystemMXBean): Double {
        return (os as com.sun.management.OperatingSystemMXBean).processCpuTime.toDouble()
    }

    private fun calculateVPSFreeMemory(os: OperatingSystemMXBean): Double {
        return (os as com.sun.management.OperatingSystemMXBean).freePhysicalMemorySize / gb
    }

    private fun calculateVPSMaxMemory(os: OperatingSystemMXBean): Double {
        return (os as com.sun.management.OperatingSystemMXBean).totalPhysicalMemorySize / gb
    }

    private fun getVpsCPUUsage(os: OperatingSystemMXBean): Double {
        vpsCPUUsage = (os as com.sun.management.OperatingSystemMXBean).systemCpuLoad * 100
        return vpsCPUUsage
    }
}

private fun Double.floor(factor: Double = 1.0) = Math.floor(this * factor) / factor
