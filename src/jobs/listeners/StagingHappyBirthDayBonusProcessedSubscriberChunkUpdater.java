package jobs.listeners;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

import domain.models.HappyBirthDayBonusSubscriber;

public class StagingHappyBirthDayBonusProcessedSubscriberChunkUpdater implements ItemWriteListener<HappyBirthDayBonusSubscriber> {

	@Override
	public void afterWrite(List<? extends HappyBirthDayBonusSubscriber> items) {
		// TODO Auto-generated method stub
		/*The method afterWrite takes the same list of items that were previously written by the ItemWriter.
		Using this lets you update the staged records to be flagged as processed.*/

		for (@SuppressWarnings("unused") HappyBirthDayBonusSubscriber subscriber : items) {
			/*update("update " + tableName + SQL + whereClause,  new Object[] {accountTransaction.getId()});*/
		}
	}

	@Override
	public void beforeWrite(List<? extends HappyBirthDayBonusSubscriber> items) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWriteError(Exception exception, List<? extends HappyBirthDayBonusSubscriber> items) {
		// TODO Auto-generated method stub

	}

}
