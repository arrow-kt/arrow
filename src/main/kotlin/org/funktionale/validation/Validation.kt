package org.funktionale.validation

import org.funktionale.either.Disjunction

class Validation<out E : Any>(vararg disjunctionSequence: Disjunction<E, *>) {

    val failures: List<E> = disjunctionSequence.filter { it.isLeft() }.map { it.swap().get() }

    val hasFailures: Boolean = failures.isNotEmpty()

}

fun <L : Any, R : Any, R1 : Any, R2 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        ifValid: (R1, R2) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        ifValid: (R1, R2, R3) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        ifValid: (R1, R2, R3, R4) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        ifValid: (R1, R2, R3, R4, R5) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        ifValid: (R1, R2, R3, R4, R5, R6) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        p15: Disjunction<L, R15>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        p15: Disjunction<L, R15>,
        p16: Disjunction<L, R16>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        p15: Disjunction<L, R15>,
        p16: Disjunction<L, R16>,
        p17: Disjunction<L, R17>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        p15: Disjunction<L, R15>,
        p16: Disjunction<L, R16>,
        p17: Disjunction<L, R17>,
        p18: Disjunction<L, R18>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any, R19 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        p15: Disjunction<L, R15>,
        p16: Disjunction<L, R16>,
        p17: Disjunction<L, R17>,
        p18: Disjunction<L, R18>,
        p19: Disjunction<L, R19>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get(), p19.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any, R19 : Any, R20 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        p15: Disjunction<L, R15>,
        p16: Disjunction<L, R16>,
        p17: Disjunction<L, R17>,
        p18: Disjunction<L, R18>,
        p19: Disjunction<L, R19>,
        p20: Disjunction<L, R20>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19, R20) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get(), p19.get(), p20.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any, R19 : Any, R20 : Any, R21 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        p15: Disjunction<L, R15>,
        p16: Disjunction<L, R16>,
        p17: Disjunction<L, R17>,
        p18: Disjunction<L, R18>,
        p19: Disjunction<L, R19>,
        p20: Disjunction<L, R20>,
        p21: Disjunction<L, R21>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19, R20, R21) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get(), p19.get(), p20.get(), p21.get()))
    }
}

fun <L : Any, R : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any, R6 : Any, R7 : Any, R8 : Any, R9 : Any, R10 : Any, R11 : Any, R12 : Any, R13 : Any, R14 : Any, R15 : Any, R16 : Any, R17 : Any, R18 : Any, R19 : Any, R20 : Any, R21 : Any, R22 : Any> validate(
        p1: Disjunction<L, R1>,
        p2: Disjunction<L, R2>,
        p3: Disjunction<L, R3>,
        p4: Disjunction<L, R4>,
        p5: Disjunction<L, R5>,
        p6: Disjunction<L, R6>,
        p7: Disjunction<L, R7>,
        p8: Disjunction<L, R8>,
        p9: Disjunction<L, R9>,
        p10: Disjunction<L, R10>,
        p11: Disjunction<L, R11>,
        p12: Disjunction<L, R12>,
        p13: Disjunction<L, R13>,
        p14: Disjunction<L, R14>,
        p15: Disjunction<L, R15>,
        p16: Disjunction<L, R16>,
        p17: Disjunction<L, R17>,
        p18: Disjunction<L, R18>,
        p19: Disjunction<L, R19>,
        p20: Disjunction<L, R20>,
        p21: Disjunction<L, R21>,
        p22: Disjunction<L, R22>,
        ifValid: (R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19, R20, R21, R22) -> R
): Disjunction<List<L>, R> {
    val validation = Validation(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22)
    return if (validation.hasFailures) {
        Disjunction.Left(validation.failures)
    } else {
        Disjunction.Right(ifValid(p1.get(), p2.get(), p3.get(), p4.get(), p5.get(), p6.get(), p7.get(), p8.get(), p9.get(), p10.get(), p11.get(), p12.get(), p13.get(), p14.get(), p15.get(), p16.get(), p17.get(), p18.get(), p19.get(), p20.get(), p21.get(), p22.get()))
    }
}

