package team1.PageRankScoringFilterPlugin;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Nutch;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.plugin.PluginRepository;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.scoring.ScoringFilter;
import org.apache.nutch.scoring.ScoringFilterException;

public class PageRankScoringFilter implements ScoringFilter{



	private ScoringFilter[] filters;
	private Configuration conf;

	public PageRankScoringFilter(Configuration conf) {
		super();
		//load all filters
		this.filters = (ScoringFilter[]) PluginRepository.get(conf)
				.getOrderedPlugins(ScoringFilter.class, ScoringFilter.X_POINT_ID,
						"scoring.filter.order");
	}

	/** Calculate a sort value for Generate. */
	public float generatorSortValue(Text url, CrawlDatum datum, float initSort)
			throws ScoringFilterException {
		for (int i = 0; i < this.filters.length; i++) {
			//generate sort values only for PagrRankScoringFilter(Custom defined filter)
			if(this.filters[i].getClass() == PageRankScoringFilter.class)
			{
				initSort = this.filters[i].generatorSortValue(url, datum, initSort);
			}
		}
		return initSort;
	}

	/** Calculate a new initial score, used when adding newly discovered pages. */
	public void initialScore(Text url, CrawlDatum datum)
			throws ScoringFilterException {
		for (int i = 0; i < this.filters.length; i++) {
			//calculate initial score only for PagrRankScoringFilter(Custom defined filter)
			if(this.filters[i].getClass() == PageRankScoringFilter.class)
			{
				this.filters[i].initialScore(url, datum);
			}

		}
	}

	/** Calculate a new initial score, used when injecting new pages. */
	public void injectedScore(Text url, CrawlDatum datum)
			throws ScoringFilterException {
		for (int i = 0; i < this.filters.length; i++) {
			//generate sort values only for PagrRankScoringFilter(Custom defined filter)
			if(this.filters[i].getClass() == PageRankScoringFilter.class)
			{
				this.filters[i].injectedScore(url, datum);
			}

		}
	}

	/** Calculate updated page score during CrawlDb.update(). */
	public void updateDbScore(Text url, CrawlDatum old, CrawlDatum datum,
			List<CrawlDatum> inlinked) throws ScoringFilterException {

		PageRankAlgorithm pageRankAlgo = new PageRankAlgorithm(10, 0.1, 0.85);


		for (int i = 0; i < this.filters.length; i++) {
			//update page score only for PagrRankScoringFilter(Custom defined filter)
			if(this.filters[i].getClass() == PageRankScoringFilter.class)
			{
				//get page rank score for the current page form custom defined page rank algorithm 
				Map<String, Double> finalPageRank = pageRankAlgo.getFinalPageRankMap();
				Double pageRank = finalPageRank.get(url);
				
				//update the previous score of page by page rank score of that page 
				datum.setScore(pageRank.floatValue());
				this.filters[i].updateDbScore(url, old, datum, inlinked);	
			}
		}
	}

	public void passScoreBeforeParsing(Text url, CrawlDatum datum, Content content)
			throws ScoringFilterException {
		for (int i = 0; i < this.filters.length; i++) {
			//pass scores before parsing only for PagrRankScoringFilter(Custom defined filter)
			if(this.filters[i].getClass() == PageRankScoringFilter.class)
			{
				this.filters[i].passScoreBeforeParsing(url, datum, content);
			}

		}
	}

	public void passScoreAfterParsing(Text url, Content content, Parse parse)
			throws ScoringFilterException {
		for (int i = 0; i < this.filters.length; i++) {
			//pass scores after parsing only for PagrRankScoringFilter(Custom defined filter)
			if(this.filters[i].getClass() == PageRankScoringFilter.class)
			{
				this.filters[i].passScoreAfterParsing(url, content, parse);
			}
		}
	}

	public CrawlDatum distributeScoreToOutlinks(Text fromUrl,
			ParseData parseData, Collection<Entry<Text, CrawlDatum>> targets,
			CrawlDatum adjust, int allCount) throws ScoringFilterException {
		for (int i = 0; i < this.filters.length; i++) {
			//apply this method only for PagrRankScoringFilter(Custom defined filter)
			if(this.filters[i].getClass() == PageRankScoringFilter.class)
			{
				adjust = this.filters[i].distributeScoreToOutlinks(fromUrl, parseData,
						targets, adjust, allCount);
			}
		}
		return adjust;
	}

	public float indexerScore(Text url, NutchDocument doc, CrawlDatum dbDatum,
			CrawlDatum fetchDatum, Parse parse, Inlinks inlinks, float initScore)
					throws ScoringFilterException {
		for (int i = 0; i < this.filters.length; i++) {
			//apply this method only for PagrRankScoringFilter(Custom defined filter)
			if(this.filters[i].getClass() == PageRankScoringFilter.class)
			{
				initScore = this.filters[i].indexerScore(url, doc, dbDatum, fetchDatum,
						parse, inlinks, initScore);
			}
		}
		return initScore;
	}

	//get configuration of filters
	public Configuration getConf() {
		return conf;
	}

	//set configuration of filters
	public void setConf(Configuration conf) {
		this.conf = conf;

	}		 
}
