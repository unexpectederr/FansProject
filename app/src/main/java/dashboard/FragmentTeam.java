package dashboard;

import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.util.ArrayList;

import SvgHelper.SvgDecoder;
import SvgHelper.SvgDrawableTranscoder;
import SvgHelper.SvgSoftwareLayerSetter;
import adapters.LeagueTableAdapter;
import digitalbath.fansproject.R;
import models.team_data.Fixture;
import models.team_data.Fixtures;
import models.team_data.LeagueTable;
import models.team_data.TeamInfo;
import networking.TeamAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by unexpected_err on 29/04/2017.
 */

public class FragmentTeam extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView leagueTableRecycler;
    private TextView awayTeamName;
    private TextView homeTeamPastMatch;
    private TextView awayTeamPastMatch;
    private TextView homeTeamPastMatchGoals;
    private TextView awayTeamPastMatchGoals;
    private TextView homeTeam2PastMatch;
    private TextView awayTeam2PastMatch;
    private TextView homeTeam2PastMatchGoals;
    private TextView awayTeam2PastMatchGoals;
    private TextView matchDate;
    private ImageView awayTeamLogo;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    public FragmentTeam() {
    }

    public static FragmentTeam newInstance(int sectionNumber) {

        FragmentTeam fragment = new FragmentTeam();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team, container, false);

        initializeViews(rootView);

        initializeSVGHelper();

        initializeRecyclerView(rootView);

        getLeagueTable();

        getTeamFixtures();

        return rootView;
    }

    private void initializeViews(View rootView) {
        awayTeamLogo = (ImageView) rootView.findViewById(R.id.away_team_logo);
        homeTeamPastMatch = (TextView) rootView.findViewById(R.id.home_team_past_matches);
        awayTeamPastMatch = (TextView) rootView.findViewById(R.id.away_team_past_matches);
        homeTeamPastMatchGoals = (TextView) rootView.findViewById(R.id.home_team_past_matches_points);
        awayTeamPastMatchGoals = (TextView) rootView.findViewById(R.id.away_team_past_matches_points);
        homeTeam2PastMatch = (TextView) rootView.findViewById(R.id.home_team2_past_matches);
        awayTeam2PastMatch = (TextView) rootView.findViewById(R.id.away_team2_past_matches);
        homeTeam2PastMatchGoals = (TextView) rootView.findViewById(R.id.home_team2_past_matches_points);
        awayTeam2PastMatchGoals = (TextView) rootView.findViewById(R.id.away_team2_past_matches_points);
        awayTeamName = (TextView) rootView.findViewById(R.id.away_team);
        matchDate = (TextView) rootView.findViewById(R.id.match_date_time);
    }

    private void initializeSVGHelper() {
        requestBuilder = Glide.with(getContext())
                .using(Glide.buildStreamModelLoader(Uri.class, getContext()), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());
    }

    private void initializeRecyclerView(View rootView) {
        leagueTableRecycler = (RecyclerView) rootView.findViewById(R.id.league_table_recycler);
        leagueTableRecycler.setNestedScrollingEnabled(false);
        leagueTableRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getTeamData(int teamId) {
        TeamAPI.service.getTeamData(teamId).enqueue(new Callback<TeamInfo>() {
            @Override
            public void onResponse(Call<TeamInfo> call, Response<TeamInfo> response) {
                Uri uri = Uri.parse(response.body().getCrestUrl());

                requestBuilder
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .load(uri)
                        .into(awayTeamLogo);

            }

            @Override
            public void onFailure(Call<TeamInfo> call, Throwable t) {
            }
        });
    }

    private void getLeagueTable() {
        TeamAPI.service.getLeagueTable().enqueue(new Callback<LeagueTable>() {
            @Override
            public void onResponse(Call<LeagueTable> call, Response<LeagueTable> response) {

                initializeLeagueTableAdapter(response);

                TextView matchDay = (TextView) getActivity().findViewById(R.id.match_day);
                matchDay.setText(" (Match day " + String.valueOf(response.body().getMatchDay()) + ")");
            }

            @Override
            public void onFailure(Call<LeagueTable> call, Throwable t) {
            }
        });
    }

    private void initializeLeagueTableAdapter(Response<LeagueTable> response) {
        LeagueTableAdapter leagueTableAdapter = new LeagueTableAdapter(getContext(), response.body());
        leagueTableRecycler.setAdapter(leagueTableAdapter);
    }

    private void getTeamFixtures() {
        TeamAPI.service.getTeamFixtures().enqueue(new Callback<Fixtures>() {
            @Override
            public void onResponse(Call<Fixtures> call, Response<Fixtures> response) {

                findNextMatch(response);

            }

            @Override
            public void onFailure(Call<Fixtures> call, Throwable t) {
            }
        });
    }

    private void findNextMatch(Response<Fixtures> response) {

        ArrayList<Fixture> fixtures = response.body().getFixtures();

        if (fixtures.get(0).getStatus() == null) {

            String awayTeamId = fixtures.get(0).getLinks().getAwayTeam().getHref().split("teams/")[1];
            matchDate.setText(fixtures.get(0).getDate());

            if (!fixtures.get(0).getAwayTeamName().contains("Juventus")) {
                awayTeamName.setText(fixtures.get(0).getAwayTeamName());
            } else {
                awayTeamName.setText(fixtures.get(0).getHomeTeamName());
            }

            setFirstPastMatchUnknown();

            setSecondPastMatchUnknown();

            getTeamData(Integer.parseInt(awayTeamId));
        } else {
            for (int i = 0; i < response.body().getFixtures().size(); i++) {

                if (fixtures.get(i).getStatus().equals("TIMED")) {

                    matchDate.setText(fixtures.get(i).getDate());
                    String awayTeamId;

                    if (!fixtures.get(i).getAwayTeamName().contains("Juventus")) {
                        awayTeamId = fixtures.get(i).getLinks().getAwayTeam().getHref().split("teams/")[1];
                    } else {
                        awayTeamId = fixtures.get(i).getLinks().getHomeTeam().getHref().split("teams/")[1];
                    }

                    getTeamData(Integer.parseInt(awayTeamId));

                    boolean isFirstMatch = i == 0;
                    boolean isSecondMatch = i == 1;

                    if (isFirstMatch) {

                        setFirstPastMatchUnknown();

                        setSecondPastMatchUnknown();
                    }

                    if (isSecondMatch) {

                        getFirstPastMatch(fixtures, i);

                        setSecondPastMatchUnknown();
                    }

                    if (i > 1) {

                        getFirstPastMatch(fixtures, i);

                        getSecondPastMatch(fixtures, i);
                    }
                }
            }
        }
    }

    private void getFirstPastMatch(ArrayList<Fixture> fixtures, int i) {
        homeTeamPastMatch.setText(fixtures.get(i - 1).getHomeTeamName());
        awayTeamPastMatch.setText(fixtures.get(i - 1).getAwayTeamName());
        homeTeamPastMatchGoals.setText(fixtures.get(i - 1).getResult().getGoalsHomeTeam());
        awayTeamPastMatchGoals.setText(fixtures.get(i - 1).getResult().getGoalsAwayTeam());
    }

    private void getSecondPastMatch(ArrayList<Fixture> fixtures, int i) {
        homeTeam2PastMatch.setText(fixtures.get(i - 2).getHomeTeamName());
        awayTeam2PastMatch.setText(fixtures.get(i - 2).getAwayTeamName());
        homeTeam2PastMatchGoals.setText(fixtures.get(i - 2).getResult().getGoalsHomeTeam());
        awayTeam2PastMatchGoals.setText(fixtures.get(i - 2).getResult().getGoalsAwayTeam());
    }

    private void setFirstPastMatchUnknown() {
        homeTeamPastMatch.setText("N/A");
        awayTeamPastMatch.setText("N/A");
        homeTeamPastMatchGoals.setText("N/A");
        awayTeamPastMatchGoals.setText("N/A");
    }

    private void setSecondPastMatchUnknown() {
        homeTeam2PastMatch.setText("N/A");
        awayTeam2PastMatch.setText("N/A");
        homeTeam2PastMatchGoals.setText("N/A");
        awayTeam2PastMatchGoals.setText("N/A");
    }

}