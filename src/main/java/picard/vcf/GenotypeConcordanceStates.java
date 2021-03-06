package picard.vcf;

import java.util.HashMap;
import java.util.Map;

import static picard.vcf.GenotypeConcordanceStateCodes.*;
/**
 * A class to store the various classifications for:
 * 1. a truth genotype versus a reference
 * 2. a call genotype versus a truth, relative to a reference
 *
 * An example use of this class is to have one instance per following use case:
 * - SNPs
 * - indels
 * - filtered variant (truth or call)
 * - filtered genotype (truth or call)
 * - low GQ (call)
 * - low DP (call)
 * - No call (truth or call)
 * - No variant (truth or call) *
 *
 * @author nhomer
 */
public class GenotypeConcordanceStates {

    static final Map<Integer, TruthState> truthMap = TruthState.getCodeMap();
    static final Map<Integer, CallState> callMap   = CallState.getCodeMap();

    /**
     * These states represent the relationship between a truth genotype and the reference sequence.
     */
    public enum TruthState {
        MISSING (MISSING_CODE.ordinal()),
        HOM_REF (HOM_REF_CODE.ordinal()), // ref/ref
        HET_REF_VAR1 (HET_REF_VAR1_CODE.ordinal()), // ref/var1 (var1!=ref)
        HET_VAR1_VAR2 (HET_VAR1_VAR2_CODE.ordinal()), // var1/var2 (var1!=var2, var1!=ref, var2!=ref)
        HOM_VAR1 (HOM_VAR1_CODE.ordinal()), // var1/var1 (var1!=ref)
        NO_CALL (NO_CALL_CODE.ordinal()),
        LOW_GQ (LOW_GQ_CODE.ordinal()),
        LOW_DP (LOW_DP_CODE.ordinal()),
        VC_FILTERED (VC_FILTERED_CODE.ordinal()),
        GT_FILTERED (GT_FILTERED_CODE.ordinal()),
        IS_MIXED (IS_MIXED_CODE.ordinal());

        public static TruthState getHom(final int alleleIdx) {
            if (alleleIdx == 0) return HOM_REF;
            if (alleleIdx == 1) return HOM_VAR1;
            assert false;
            return null;
        }

        public static TruthState getVar(final int allele0idx, final int allele1idx) {
            if (allele0idx == 0 && allele1idx == 1) return HET_REF_VAR1;
            if (allele0idx == 1 && allele1idx == 0) return HET_REF_VAR1;

            if (allele0idx == 1 && allele1idx == 2) return HET_VAR1_VAR2;
            if (allele0idx == 2 && allele1idx == 1) return HET_VAR1_VAR2;

            assert false;
            return null;
        }

        static Map<Integer, TruthState> getCodeMap() {
            final Map<Integer, TruthState> map = new HashMap<>();
            final TruthState truthValues[] = TruthState.values();
            for (int i = 0; i < truthValues.length; i++) {
                map.put(truthValues[i].code, truthValues[i]);
            }
            return map;
        }

        private final int code;

        TruthState(final int code) {
            this.code = code;
        }

        public int getCode() { return this.code; }
    }

    /**
     * These states represent the relationship between the call genotype and the truth genotype relative to
     * a reference sequence.
     * The Enum constants must be in the same order as the truth state to allow for comparison.
     */
    public enum CallState {
        MISSING (MISSING_CODE.ordinal()),
        HOM_REF (HOM_REF_CODE.ordinal()), // ref/ref, valid for all TruthStates
        HET_REF_VAR1 (HET_REF_VAR1_CODE.ordinal()), // ref/var1, valid for all TruthStates
        HET_VAR1_VAR2 (HET_VAR1_VAR2_CODE.ordinal()), // var1/var2, valid for all TruthStates
        HOM_VAR1 (HOM_VAR1_CODE.ordinal()), // var1/var1, valid for all TruthStates
        HET_REF_VAR2 (INCOMPARABLE_CODE.ordinal()), // ref/var2, valid only for TruthStates: HET_REF_VAR1, HET_VAR1_VAR2, HOM_VAR1
        HET_REF_VAR3 (INCOMPARABLE_CODE.ordinal()), // ref/var3, valid only for TruthStates: HET_VAR1_VAR2
        HET_VAR1_VAR3 (INCOMPARABLE_CODE.ordinal()), // var1/var3, valid only for TruthStates: HET_VAR1_VAR2. also encapsulates HET_VAR2_VAR3 (see special case below)
        HET_VAR3_VAR4 (INCOMPARABLE_CODE.ordinal()), // var3/var4, valid only for TruthStates: HET_REF_VAR1, HET_VAR1_VAR2, HOM_VAR1
        HOM_VAR2 (INCOMPARABLE_CODE.ordinal()), // var2/var2, valid only for TruthStates: HET_REF_VAR1, HET_VAR1_VAR2, HOM_VAR1
        HOM_VAR3 (INCOMPARABLE_CODE.ordinal()), // var3/var3, valid only for TruthStates: HET_VAR1_VAR2
        NO_CALL (NO_CALL_CODE.ordinal()),
        LOW_GQ (LOW_GQ_CODE.ordinal()),
        LOW_DP (LOW_DP_CODE.ordinal()),
        VC_FILTERED (VC_FILTERED_CODE.ordinal()),
        GT_FILTERED (GT_FILTERED_CODE.ordinal()),
        IS_MIXED (IS_MIXED_CODE.ordinal());


        public static CallState getHom(final int alleleIdx) {
            if (alleleIdx == 0) return HOM_REF;
            if (alleleIdx == 1) return HOM_VAR1;
            if (alleleIdx == 2) return HOM_VAR2;
            if (alleleIdx == 3) return HOM_VAR3;

            assert false;
            return null;
        }

        public static CallState getHet(int allele0idx, int allele1idx) {

            if(allele0idx > allele1idx){
                final int temp = allele0idx;
                allele0idx=allele1idx;
                allele1idx=temp;
            }
            if(allele0idx == 0) { //REF CASE
                if (allele1idx == 1) return HET_REF_VAR1;
                if (allele1idx == 2) return HET_REF_VAR2;
                if (allele1idx == 3) return HET_REF_VAR3;
                assert false;
                return null;
            }

            //HET CASES
            if(allele0idx == 1) {
                if (allele1idx == 2) return HET_VAR1_VAR2;
                if (allele1idx == 3) return HET_VAR1_VAR3;
                assert false;
                return null;
            }

            if(allele0idx == 2 && allele1idx == 3) return HET_VAR3_VAR4; //special case not a mistake.
            if(allele0idx == 3 && allele1idx == 4) return HET_VAR3_VAR4;

            assert false;
            return null;
        }

        static Map<Integer, CallState> getCodeMap() {
            final Map<Integer, CallState> map = new HashMap<>();
            final CallState callValues[] = CallState.values();
            for (int i = 0; i < callValues.length; i++) {
                map.put(callValues[i].code, callValues[i]);
            }
            return map;
        }
        private final int code;

        private CallState(final int code) {
            this.code = code;
        }

        public int getCode() { return this.code; }
    }

    /**
     * A specific state for a 2x2 contingency table.
     * NA denotes an invalid state that should not be reachable by the code.
     * EMPTY denotes that no conclusion could be drawn from the data.
     */
    public enum ContingencyState {
        TP,
        FP,
        TN,
        FN,
        NA,
        EMPTY
    }

    /**
     * A minute class to store the truth and call state respectively.
     */
    public static class TruthAndCallStates implements Comparable<TruthAndCallStates>{
        public final TruthState truthState;
        public final CallState callState;

        public TruthAndCallStates(final TruthState truthState, final CallState callState) {
            this.truthState = truthState;
            this.callState = callState;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return compareTo((TruthAndCallStates) o) == 0;
        }

        @Override
        public int hashCode() {
            int result = truthState.hashCode();
            result = 31 * result + callState.hashCode();
            return result;
        }

        @Override
        public int compareTo(final TruthAndCallStates that) {
            int result = this.truthState.compareTo(that.truthState);
            if (result == 0) result = this.callState.compareTo(that.callState);
            return result;
        }
    }
}
