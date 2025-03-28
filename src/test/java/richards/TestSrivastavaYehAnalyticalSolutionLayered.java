/*
  * GNU GPL v3 License
 *
 * Copyright 2020 Niccolo` Tubini
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package richards;

import java.net.URISyntaxException;
import java.util.*;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;

import it.geoframe.blogspot.buffer.buffertowriter.RichardsBuffer1D;
import it.geoframe.blogspot.whetgeo1d.richardssolver.RichardsSolver1DMain;
import it.geoframe.blogpsot.netcdf.monodimensionalproblemtimedependent.ReadNetCDFRichardsGrid1D;
import it.geoframe.blogpsot.netcdf.monodimensionalproblemtimedependent.ReadNetCDFRichardsOutput1D;
import it.geoframe.blogpsot.netcdf.monodimensionalproblemtimedependent.WriteNetCDFRichards1DDouble;

import org.junit.Test;

/**
 * Test the {@link TestSrivastavaYehAnalyticalSolutionLayered} module.
 * 
 * This test consider an initial hydrostatic condition with Dirichlet boundary condition at the 
 * top and free drainage at the bottom. 
 * 
 * @author Niccolo' Tubini
 */
public class TestSrivastavaYehAnalyticalSolutionLayered {

	@Test
	public void Test() throws Exception {


		String startDate = "2020-01-01 00:00";
		String endDate = "2020-01-09 00:00";
		int timeStepMinutes = 1;
		String fId = "ID";
				
		String pathTopBC = "resources/input/TimeSeries/SrivastavaYeh_q09.csv"; 
		String pathBottomBC = "resources/input/TimeSeries/SrivastavaYeh_psi0.csv";
		String pathSaveDates = "resources/input/TimeSeries/SrivastavaYeh_save.csv"; 
		String pathGrid =  "resources/input/Grid_NetCDF/SrivastavaYeh_layered_2000.nc";
		String pathOutput = "resources/output/SrivastavaYeh_layered_harmonic.nc";
		
		String topBC = "Top Neumann";
		String bottomBC = "Bottom Dirichlet";

		String outputDescription = "\n"
				+ "Comparison with Srivastava and Yeh 1991 analytical solution.\nLayered soil, wetting case, alpha=0.1 [cm-1].\n		"
				+ "DeltaT: 60s\n		"
				+ "Interface: harmonic mean\n		"
				+ "Picard iteration: 2\n		";
		
		int writeFrequency = 2880;
		
		OmsTimeSeriesIteratorReader topBCReader = getTimeseriesReader(pathTopBC, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader bottomBCReader = getTimeseriesReader(pathBottomBC, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader saveDatesReader = getTimeseriesReader(pathSaveDates, fId, startDate, endDate, timeStepMinutes);

		RichardsBuffer1D buffer = new RichardsBuffer1D();
		WriteNetCDFRichards1DDouble writeNetCDF = new WriteNetCDFRichards1DDouble();
		ReadNetCDFRichardsGrid1D readNetCDF = new ReadNetCDFRichardsGrid1D();
		
		RichardsSolver1DMain R1DSolver = new RichardsSolver1DMain();
		
		
		readNetCDF.richardsGridFilename = pathGrid;
		
		readNetCDF.read();
		
		
		R1DSolver.z = readNetCDF.z;
		R1DSolver.spaceDeltaZ = readNetCDF.spaceDelta;
		R1DSolver.psiIC = readNetCDF.psiIC;
		R1DSolver.temperature = readNetCDF.temperature;
		R1DSolver.controlVolume = readNetCDF.controlVolume;
		R1DSolver.ks = readNetCDF.Ks;
		R1DSolver.thetaS = readNetCDF.thetaS;
		R1DSolver.thetaR = readNetCDF.thetaR;
		R1DSolver.par1SWRC = readNetCDF.par1SWRC;
		R1DSolver.par2SWRC = readNetCDF.par2SWRC;
		R1DSolver.par3SWRC = readNetCDF.par3SWRC;
		R1DSolver.par4SWRC = readNetCDF.par4SWRC;
		R1DSolver.par5SWRC = readNetCDF.par5SWRC;
		R1DSolver.alphaSpecificStorage = readNetCDF.alphaSS;
		R1DSolver.betaSpecificStorage = readNetCDF.betaSS;
		R1DSolver.inEquationStateID = readNetCDF.equationStateID;
		R1DSolver.inParameterID = readNetCDF.parameterID;
		R1DSolver.beta0 = -766.45;
		R1DSolver.referenceTemperatureSWRC = 278.15;
		R1DSolver.maxPonding = 0.0;
		R1DSolver.typeClosureEquation = new String[] {"Gardner"};
		R1DSolver.typeEquationState = new String[] {"Gardner"};
		R1DSolver.typeUHCModel = new String[] {"Gardner"};
		R1DSolver.typeUHCTemperatureModel = "notemperature"; 
		R1DSolver.interfaceHydraulicConductivityModel = "Harmonic mean";
		R1DSolver.topBCType = topBC;
		R1DSolver.bottomBCType = bottomBC;
		R1DSolver.delta = 0;
		R1DSolver.tTimeStep = 60;
		R1DSolver.timeDelta = 60;
		R1DSolver.newtonTolerance = Math.pow(10,-12);
		R1DSolver.nestedNewton = 1;
		R1DSolver.picardIteration = 2;

		buffer.writeFrequency = writeFrequency;
		
		writeNetCDF.fileName = pathOutput;
		writeNetCDF.briefDescritpion = outputDescription;
		writeNetCDF.pathGrid = pathGrid;
		writeNetCDF.pathBottomBC = pathBottomBC; 
		writeNetCDF.pathTopBC = pathTopBC; 
		writeNetCDF.bottomBC = bottomBC;
		writeNetCDF.topBC = topBC;
		writeNetCDF.swrcModel = "Gardener";
		writeNetCDF.soilHydraulicConductivityModel = "Gardner";
		writeNetCDF.interfaceConductivityModel = "harmonic mean";
		writeNetCDF.writeFrequency = writeFrequency;
		writeNetCDF.spatialCoordinate = readNetCDF.eta;
		writeNetCDF.dualSpatialCoordinate = readNetCDF.etaDual;	
		writeNetCDF.controlVolume = readNetCDF.controlVolume;
		writeNetCDF.psiIC = readNetCDF.psiIC;
		writeNetCDF.temperature = readNetCDF.temperature;
		writeNetCDF.outVariables = new String[] {"darcyVelocity"};
		writeNetCDF.timeUnits = "Minutes since 01/01/1970 00:00:00 UTC";
		writeNetCDF.timeZone = "UTC"; 
		writeNetCDF.fileSizeMax = 350;
		
		while( topBCReader.doProcess  ) {
		
			
			topBCReader.nextRecord();	
			HashMap<Integer, double[]> bCValueMap = topBCReader.outData;
			R1DSolver.inTopBC= bCValueMap;


			bottomBCReader.nextRecord();
			bCValueMap = bottomBCReader.outData;
			R1DSolver.inBottomBC = bCValueMap;

			saveDatesReader.nextRecord();
			bCValueMap = saveDatesReader.outData;
			R1DSolver.inSaveDate = bCValueMap;
			
			R1DSolver.inCurrentDate = topBCReader.tCurrent;
			
			R1DSolver.solve();

			
			buffer.inputDate = R1DSolver.inCurrentDate;
			buffer.doProcessBuffer = R1DSolver.doProcessBuffer;
			buffer.inputVariable = R1DSolver.outputToBuffer;
			
			buffer.solve();
			

			writeNetCDF.variables = buffer.myVariable;
			writeNetCDF.doProcess = topBCReader.doProcess;
			writeNetCDF.writeNetCDF();


		}

		topBCReader.close();
		bottomBCReader.close();
				
		/*
		 * ASSERT 
		 */
		System.out.println("Assert");
		ReadNetCDFRichardsOutput1D readTestData = new ReadNetCDFRichardsOutput1D();
		readTestData.richardsOutputFilename = "resources/Output/Check_SrivastavaYeh_layered_harmonic_0001.nc";
		readTestData.read();
		
		ReadNetCDFRichardsOutput1D readSimData = new ReadNetCDFRichardsOutput1D();
		readSimData.richardsOutputFilename = pathOutput.replace(".nc","_0001.nc");
		readSimData.read();

		for(int k=0; k<readSimData.psi[(readSimData.psi.length)-1].length; k++) {
			if(Math.abs(readSimData.psi[(readSimData.psi.length)-1][k]-readTestData.psi[(readTestData.psi.length)-1][k])>Math.pow(10,-7)) {
				System.out.println("\n\n\t\tERROR: psi mismatch");
			}
		}

	}

	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = startDate;
		reader.tTimestep = timeStepMinutes;
		reader.tEnd = endDate;
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}
