USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_NationalUpdateInsertTrainingVector]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		gjm
-- =============================================
CREATE PROCEDURE [svm].[SP_NationalUpdateInsertTrainingVector]

	@allUniqueConcepts  varchar(max),
	@trainingType  varchar(50)
	
	AS
BEGIN
	SET NOCOUNT ON;
	   UPDATE svm.NationalTrainingVector SET TermVector = @allUniqueConcepts
    where TermVectorType = @trainingType 
	IF @@ROWCOUNT = 0
	BEGIN      
		   INSERT INTO svm.NationalTrainingVector VALUES(@allUniqueConcepts,@trainingType)
    END 
END
GO
