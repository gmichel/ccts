USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_UpdateInsertTrainingVector]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		gjm
-- =============================================
CREATE PROCEDURE [svm].[SP_UpdateInsertTrainingVector]

	@station int,
	@allUniqueConcepts  varchar(max),
	@trainingType  varchar(50)
	
	AS
BEGIN
	SET NOCOUNT ON;
	   UPDATE svm.TrainingVector SET TermVector = @allUniqueConcepts
    where TermVectorType = @trainingType and Station=@station
	IF @@ROWCOUNT = 0
	BEGIN      
		   INSERT INTO svm.TrainingVector VALUES(@station,@allUniqueConcepts,@trainingType)
    END 
END
GO
