package jobs.listeners;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

import domain.models.BirthdayBonusSubscriber;

public class StagingHappyBirthdayBonusProcessedSubscriberChunkUpdater implements ItemWriteListener<BirthdayBonusSubscriber> {

	@Override
	public void afterWrite(List<? extends BirthdayBonusSubscriber> items) {
		// TODO Auto-generated method stub
		/*The method afterWrite takes the same list of items that were previously written by the ItemWriter.
		Using this lets you update the staged records to be flagged as processed.*/

		for (@SuppressWarnings("unused") BirthdayBonusSubscriber subscriber : items) {
			/*update("update " + tableName + SQL + whereClause,  new Object[] {accountTransaction.getId()});*/
		}
	}

	@Override
	public void beforeWrite(List<? extends BirthdayBonusSubscriber> items) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWriteError(Exception exception, List<? extends BirthdayBonusSubscriber> items) {
		// TODO Auto-generated method stub

	}

}
