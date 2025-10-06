package com.flindigital.watermeter.data

import com.flindigital.watermeter.data.model.Customer

object DummyCustomers {
    val customers: List<Customer> = listOf(
        Customer(
            userId = "081000013690",
            userName = "Andi Sebastian",
            date = "Senin, 06 Okt 2025",
            time = "09:41",
            fullAddress = "Jl. Kapten Nol, Puruk Cahu",
            latitude = -1.8701,
            longitude = 114.9023,
            isRecorded = false
        ),
        Customer(
            userId = "8723560912",
            userName = "Rifqi Mufdianto",
            date = "Senin, 06 Okt 2025",
            time = "09:45",
            fullAddress = "Jl. Kapten Nol, Puruk Cahu",
            latitude = -1.8702,
            longitude = 114.9026,
            isRecorded = false
        ),
        Customer(
            userId = "1072833248",
            userName = "Daffa Kurniawan",
            date = "Senin, 06 Okt 2025",
            time = "10:02",
            fullAddress = "Jl. Kapten Nol, Puruk Cahu",
            latitude = -1.8704,
            longitude = 114.9031,
            isRecorded = false
        ),
        Customer(
            userId = "2346578734",
            userName = "Chandra Arif",
            date = "Senin, 06 Okt 2025",
            time = "10:23",
            fullAddress = "Jl. Kapten Nol, Puruk Cahu",
            latitude = -1.8708,
            longitude = 114.9036,
            isRecorded = false
        ),
        Customer(
            userId = "2364578689",
            userName = "Zaky ardiansyah",
            date = "Senin, 06 Okt 2025",
            time = "10:31",
            fullAddress = "Jl. Kapten Nol, Puruk Cahu",
            latitude = -1.8711,
            longitude = 114.9040,
            isRecorded = false
        ),
        // already recorded examples
        Customer(
            userId = "9911223344",
            userName = "Siti Rahma",
            date = "Senin, 06 Okt 2025",
            time = "08:15",
            fullAddress = "Jl. Kapten Nol, Puruk Cahu",
            latitude = -1.8680,
            longitude = 114.9001,
            isRecorded = true
        ),
        Customer(
            userId = "7788990011",
            userName = "Budi Santoso",
            date = "Senin, 06 Okt 2025",
            time = "08:32",
            fullAddress = "Jl. Kapten Nol, Puruk Cahu",
            latitude = -1.8685,
            longitude = 114.9009,
            isRecorded = true
        )
    )
}
