package blockchain.client.communications.elements.operations;

public abstract class AbstractOperation {
	
	// TAGS
	public final static int BAD_PREDECESSOR 		= 1;
	public final static int BAD_TIMSTAMP	 		= 2;
	public final static int BAD_OPERATIONS_HASH		= 3;
	public final static int BAD_CONTEXT_HASH 		= 4;
	public final static int BAD_SIGNATURE 			= 5;
	
	public abstract byte [] asByteArray();
	public abstract byte [] hashWithTag();
}
