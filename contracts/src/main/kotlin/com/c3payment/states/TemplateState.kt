package com.c3payment.states

import com.c3payment.contracts.TemplateContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(TemplateContract::class)
data class TemplateState(val data: String, override val participants: List<AbstractParty> = listOf()) : ContractState
