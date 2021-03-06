USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_NationalStoredModel]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		gjm
-- =============================================

CREATE PROCEDURE [svm].[SP_NationalStoredModel]
	@ModelOrgan varchar(50),
	@ModelValue  varchar(max),
	@ModelDateCreated datetime,
	@ModelDateUpdated datetime
	AS
BEGIN
	SET NOCOUNT ON;
   UPDATE svm.NationalModel SET ModelValue = @ModelValue , ModelDateUpdated = @ModelDateUpdated
    where ModelOrgan = @ModelOrgan 
	IF @@ROWCOUNT = 0
	BEGIN   
	insert into svm.NationalModel  values (@ModelOrgan,@ModelValue,@ModelDateCreated,@ModelDateUpdated )
    END 
END
GO
