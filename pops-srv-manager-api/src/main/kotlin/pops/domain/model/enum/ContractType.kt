package pops.domain.model.enum

enum class ContractType(val contratro: String) {
    CLT ("CLT"),
    PJ ("PJ"),
    ESTAGIO("ESTAGIO");

    override fun toString(): String {
        return contratro
    }
}