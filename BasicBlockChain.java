import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class Transaction {
    private String fromAddress;
    private String toAddress;
    private double amount;

    public Transaction(String fromAddress, String toAddress, double amount) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
    }
    public String getFromAddress() {
    	return fromAddress;
    	
    }
    public String getToAddress() {
    	return toAddress;
    }
    public double getAmount() {
    	return amount;
    }    
}

class Block {
    private int index;
    private String previousHash;
    private String presentHash;
    private LinkedList<Transaction> transactions;

    public Block(int index, String previousHash, LinkedList<Transaction> transactions) {
        this.index = index;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.presentHash = calculateHash();
    } 
	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public void setPresentHash(String presentHash) {
		this.presentHash = presentHash;
	}

	public int getIndex() {
		return index;
	}

	

	public String getPreviousHash() {
		return previousHash;
	}

	public String getPresentHash() {
		return presentHash;
	}

	public LinkedList<Transaction> getTransactions() {
		return transactions;
	}
	public String calculateHash() {
        String transactionData = "";
        for (Transaction transaction : transactions) {
            transactionData += transaction.getFromAddress() + transaction.getToAddress() + transaction.getAmount();
        }

        String dataToHash = index + previousHash + transactionData;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    
}

class Blockchain {
    private LinkedList<Block> chain;

    public Blockchain() {
        this.chain = new LinkedList<>();
        addBlock(new Block(0, "0", new LinkedList<>()));
    }

    public void addBlock(Block newBlock) {
        if (chain.isEmpty()) {
            newBlock.setPreviousHash("0");
        } else {
            Block previousBlock = getLatestBlock();
            newBlock.setPreviousHash(previousBlock.getPresentHash());
        }
        newBlock.setPresentHash(newBlock.calculateHash());
        chain.add(newBlock);
    }
    public void addBlockToChain(LinkedList<Transaction> transactions) {
        Block previousBlock = getLatestBlock();
        int newIndex = previousBlock.getIndex() + 1;
        String previousHash = previousBlock.getPresentHash();

        Block newBlock = new Block(newIndex, previousHash, transactions);
        newBlock.calculateHash();
        chain.add(newBlock);
    }

    public Block getLatestBlock() {
        return chain.getLast();
    }

	public LinkedList<Block> getChain() {
		return chain;
	}
	
	public boolean isChainValid() {
        for (int i=1;i<chain.size();i++) {
            Block currentBlock=chain.get(i);
            Block previousBlock=chain.get(i-1);
            if (currentBlock.getPreviousHash().equals(currentBlock.calculateHash())) {
                System.out.println("Block #" + i + " has an invalid hash.");
                return false;
            }
            if (!currentBlock.getPreviousHash().equals(previousBlock.getPresentHash())) {
                System.out.println("Block #" + i + " has an invalid previous hash.");
                return false;
            }
        }
        return true;
    }

}

class BasicBlockchain {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();
        LinkedList<Transaction> transactions1 = new LinkedList<>();
        transactions1.add(new Transaction("prashu", "aswini", 10.5));
        transactions1.add(new Transaction("mom", "dad", 5.0));
        blockchain.addBlockToChain(transactions1);
        LinkedList<Transaction> transactions2 = new LinkedList<>();
        transactions2.add(new Transaction("mani", "sujji", 3.2));
        blockchain.addBlockToChain(transactions2);
        for (Block block : blockchain.getChain()) {
            System.out.println("Block #" + block.getIndex());
            System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println("Present Hash: " + block.getPresentHash());
            System.out.println("Transactions:");
            for (Transaction transaction : block.getTransactions()) {
                System.out.println("From: " + transaction.getFromAddress());
                System.out.println("To: " + transaction.getToAddress());
                System.out.println("Amount: " + transaction.getAmount());
            }

            System.out.println("--------------------");
        }
        System.out.println("Is the blockchain valid? " +blockchain.isChainValid());
    }
    }

