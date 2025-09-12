package pops.domain.model.enum

enum class ContractType(val contrato: String) {
    CLT ("CLT"),
    PJ ("PJ"),
    ESTAGIO("ESTAGIO");

    override fun toString(): String {
        return contrato
    }
}