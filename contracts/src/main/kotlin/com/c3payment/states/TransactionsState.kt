package com.c3payment.states

import com.c3payment.schema.TransactionsSchemaObject
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState


data class TransactionsState(
        val sender: Party,
        val receive: Party,
        val currency: String,
        val type: String,
        val amount:Long,
        val txid:String,
        val createAt: Long,
        override val linearId: UniqueIdentifier = UniqueIdentifier()):
        LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = listOf(sender, receive)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is TransactionsSchemaObject -> TransactionsSchemaObject.PersistentClaim(
                    this.sender.name.toString(),
                    this.receive.name.toString(),
                    this.currency,
                    this.type,
                    this.amount,
                    this.txid,
                    this.createAt,
                    this.linearId.id
            )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(TransactionsSchemaObject)
}
