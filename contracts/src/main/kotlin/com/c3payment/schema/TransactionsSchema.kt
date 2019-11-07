package com.c3payment.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

object TransactionsSchema

object TransactionsSchemaObject : MappedSchema(
        schemaFamily = TransactionsSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentClaim::class.java)) {
    @Entity
    @Table(name = "transactions_states")
    class PersistentClaim(
            @Column(name = "sender")
            var sender: String,
            @Column(name = "receive")
            var receive: String,
            @Column(name = "currency")
            var currency: String,
            @Column(name = "type")
            var type: String,
            @Column(name= "amount")
            var amount: Long,
            @Column(name = "txid")
            var txid: String,
            @Column(name = "created_at")
            val createdAt: Long,
            @Column(name = "linear_id")
            var linearID: UUID

    ) : PersistentState() {
        // Default constructor required by hibernate.
        constructor(): this("", "", "", "",0L, "", 0, UUID.randomUUID())
    }
}