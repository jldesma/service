package com.paradox.service
data class Data(val data: Array<InvoiceNTF>) { }
data class InvoiceNTF(
    val acount_addres: String,
    val invoice: Int,
    val issue_date: String,
    val terms: String,
    val due_date: String,
    val booking: Int,
    val item: String,
    val amount: String,
    val zip: String,
    val writer: String,
    val project_title: String,
    val bank_name: String,
    val bank_addree: String,
    val acct_no: String,
    val routing_no: String,
    val swift_code: String,
    val note: String,
    val phone: String) { }