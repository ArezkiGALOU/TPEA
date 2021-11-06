package blockchain.client.utils;

import blockchain.client.communications.Communication;
import blockchain.client.communications.elements.Account;
import blockchain.client.communications.elements.Block;
import blockchain.client.communications.elements.State;
import blockchain.client.communications.elements.operations.AbstractOperation;
import blockchain.client.communications.elements.operations.BadContextHashOperation;
import blockchain.client.communications.elements.operations.BadOperationHashOperation;
import blockchain.client.communications.elements.operations.BadPredecessorOperation;
import blockchain.client.communications.elements.operations.BadSignatureOperation;
import blockchain.client.communications.elements.operations.BadTimestampOperation;

public interface Constants {
	
	
	// built-in types sizes in byte so *8 bits
	int LONG_SIZE 							= 8;
	int INT_SIZE 							= 4;
	int SHORT_SIZE 							= 2;
	int BYTE_SIZE 							= 1;
	
	
	// sizes
	int BLOCK_SIZE 							= Block.SIZE;
	int STATE_FIX_SIZE						= State.FIX_SIZE;
	int TAG_SIZE 							= 2;
	int SIZE_SIZE 							= 2;
	int PUBLIC_KEY_SIZE						= 32;
	int HASH_SIZE							= 32;
	int SIGNATURE_SIZE						= 64;
	int TIMESTAMP_SIZE 						= LONG_SIZE;
	int ACCOUNT_SIZE 						= Account.SIZE;
	int SEED_SIZE 							= Communication.SEED_SIZE;
	
	
	// MESSAGES TAGS
	int GET_CURRENT_HEAD 					= 1;
	int CURRENT_HEAD 						= 2;
	int GET_BLOCK 							= 3;
	int BLOCK 								= 4;
	int GET_BLOCK_OPERATIONS				= 5;
	int BLOCK_OPERATIONS					= 6;
	int GET_BLOCK_STATE 					= 7;
	int BLOCK_STATE 						= 8;
	int INJECT_OPERATION 					= 9;
	
	// MESSAGES SIZES
	int GET_LEVEL_DEPENDANT_MESSAGE_SIZE 	= Constants.TAG_SIZE + Constants.INT_SIZE;

	
	// OPERATIONS TAGS
	int BAD_PREDECESSOR    					= AbstractOperation.BAD_PREDECESSOR;
	int BAD_TIMSTAMP    					= AbstractOperation.BAD_TIMSTAMP;
	int BAD_OPERATIONS_HASH    				= AbstractOperation.BAD_OPERATIONS_HASH;
	int BAD_CONTEXT_HASH    				= AbstractOperation.BAD_CONTEXT_HASH;
	int BAD_SIGNATURE    					= AbstractOperation.BAD_SIGNATURE;
	int FIXED_PART_SIGNED_OPERATION_SIZE	= Constants.PUBLIC_KEY_SIZE + Constants.SIGNATURE_SIZE;
	
	// OPERATIONS SIZE
	int BAD_PREDECESSOR_SIZE				= BadPredecessorOperation.BAD_PREDECESSOR_SIZE;
	int BAD_TIMESTAMP_SIZE 					= BadTimestampOperation.BAD_TIMESTAMP_SIZE;
	int BAD_OPERATION_HASH_SIZE 			= BadOperationHashOperation.BAD_OPERATION_HASH_SIZE;
	int BAD_CONTEXT_HASH_SIZE 				= BadContextHashOperation.BAD_CONTEXT_HASH_SIZE;
	int BAD_SIGNATURE_SIZE 					= BadSignatureOperation.BAD_SIGNATURE_SIZE;
	
	// DURATION BETWEEN TWO BLOCKS
	int MIN_DURATION						= 600; // Seconds
}
