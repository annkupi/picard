package picard.analysis;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.ReferenceSequence;

public class RecAndRef {
    public SAMRecord rec;
    public ReferenceSequence ref;

    RecAndRef(){
        rec = null;
        ref = null;
    }

    RecAndRef(SAMRecord samRecord, ReferenceSequence referenceSequence){
        rec = samRecord;
        ref = referenceSequence;
    }

    public SAMRecord getRec(){
        return this.rec;
    }

    public ReferenceSequence getRef(){
        return this.ref;
    }

}
